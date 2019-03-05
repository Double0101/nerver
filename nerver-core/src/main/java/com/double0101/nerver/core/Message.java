package com.double0101.nerver.core;

import java.nio.ByteBuffer;

public class Message {

    private MessageBuffer messageBuffer = null;

    public long socketId = 0;

    public byte[] sharedArray = null;

    public int offset = 0;
    public int capacity = 0;
    public int length = 0;

    public Object metaData = null;

    public Message(MessageBuffer buffer) {
        this.messageBuffer = buffer;
    }

    public int writeToMessage(ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();
        while (this.length + remaining > capacity) {
            if (!this.messageBuffer.expandMessage(this)) {
                return -1;
            }
        }

        int bytesToCopy = Math.min(remaining, this.capacity - this.length);
        byteBuffer.get(this.sharedArray, this.offset + this.length, bytesToCopy);
        this.length += bytesToCopy;

        return bytesToCopy;
    }

    public int writeToMessage(byte[] byteArray) {
        return writeToMessage(byteArray, 0, byteArray.length);
    }

    public int writeToMessage(byte[] byteArray, int offset, int length) {
        int remaining = length;
        while (this.length + remaining > capacity) {
            if (!this.messageBuffer.expandMessage(this)) {
                return -1;
            }
        }

        int bytesToCopy = Math.min(remaining, this.capacity - this.length);
        System.arraycopy(byteArray, offset, this.sharedArray, this.offset + this.length, bytesToCopy);
        this.length += bytesToCopy;
        return bytesToCopy;
    }

    public void writePartialMessageToMessage(Message message, int endIndex) {
        int startIndexOfPartialMessage = message.offset + endIndex;
        int lengthOfPartialMessage = (message.offset + message.length) - endIndex;

        System.arraycopy(message.sharedArray, startIndexOfPartialMessage,
                this.sharedArray, this.offset, lengthOfPartialMessage);
    }

    public int writeToByteBuffer(ByteBuffer byteBuffer) {
        return 0;
    }
}
