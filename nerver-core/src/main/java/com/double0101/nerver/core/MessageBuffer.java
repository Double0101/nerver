package com.double0101.nerver.core;

/*
 * 开辟了三个大片的数组
 * 相当于开辟了三块内存用来存放Message
 * capacity是每一条message占用的大小
 */
public class MessageBuffer {

    public static final int KB = 1024;
    public static final int MB = 1024 * KB;

    private static final int CAPACITY_SMALL  =   4  * KB;
    private static final int CAPACITY_MEDIUM = 128  * KB;
    private static final int CAPACITY_LARGE  =   1  * MB;

    byte[]  smallMessageBuffer  = new byte[1024 *   4 * KB];
    byte[]  mediumMessageBuffer = new byte[128  * 128 * KB];
    byte[]  largeMessageBuffer  = new byte[16   *   1 * MB];

    QueueIntFlip smallMessageBufferFreeBlocks = new QueueIntFlip(1024);
    QueueIntFlip mediumMessageBufferFreeBlocks = new QueueIntFlip(128);
    QueueIntFlip largeMessageBufferFreeBlocks = new QueueIntFlip(16);

    public MessageBuffer() {
        for (int i = 0; i < smallMessageBuffer.length; i += CAPACITY_SMALL) {
            this.smallMessageBufferFreeBlocks.put(i);
        }
        for (int i = 0; i < mediumMessageBuffer.length; i += CAPACITY_MEDIUM) {
            this.mediumMessageBufferFreeBlocks.put(i);
        }
        for (int i = 0; i < largeMessageBuffer.length; i += CAPACITY_LARGE) {
            this.largeMessageBufferFreeBlocks.put(i);
        }
    }

    public Message getMessage() {
        int nextFreeSmallBlock = this.smallMessageBufferFreeBlocks.take();

        if (nextFreeSmallBlock == -1) {
            return null;
        }

        Message message = new Message(this);
        message.sharedArray = this.smallMessageBuffer;
        message.capacity = CAPACITY_SMALL;
        message.offset = nextFreeSmallBlock;
        message.length = 0;

        return message;
    }

    public boolean expandMessage(Message message) {
        if (message.capacity == CAPACITY_SMALL) {
            return moveMessage(message, this.smallMessageBufferFreeBlocks,
                    this.mediumMessageBufferFreeBlocks, this.mediumMessageBuffer, CAPACITY_MEDIUM);
        } else if (message.capacity == CAPACITY_MEDIUM) {
            return moveMessage(message, this.mediumMessageBufferFreeBlocks,
                    this.largeMessageBufferFreeBlocks, this.largeMessageBuffer, CAPACITY_LARGE);
        } else {
            return false;
        }
    }

    private boolean moveMessage(Message message, QueueIntFlip srcBlockQueue,
                                QueueIntFlip destBlockQueue, byte[] dest, int newCapacity) {
        int nextFreeBlock = destBlockQueue.take();
        if (nextFreeBlock == -1) return false;

        System.arraycopy(message.sharedArray, message.offset, dest, nextFreeBlock, message.length);

        srcBlockQueue.put(message.offset);

        message.sharedArray = dest;
        message.offset = nextFreeBlock;
        message.capacity = newCapacity;
        return true;
    }
}
