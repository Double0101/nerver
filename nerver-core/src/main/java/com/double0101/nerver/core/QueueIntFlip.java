package com.double0101.nerver.core;

public class QueueIntFlip {

    private int[] elements = null;

    private int capacity = 0;
    private int readPos = 0;
    private int writePos = 0;

    /*
     * 标记readPos和writePos的相对位置
     * if (writePos >= readPos) flipped = false
     * if (writePos < readPos) flipped = true
     */
    public boolean flipped = false;

    public QueueIntFlip(int capacity) {
        this.capacity = capacity;
        this.elements = new int[capacity];
    }

    public void reset() {
        this.writePos = 0;
        this.readPos = 0;
        this.flipped = false;
    }

    //  剩余可读
    public int available() {
        if (!flipped) {
            return writePos - readPos;
        }
        return capacity - readPos + writePos;
    }

    public int remainingCapacity() {
        if (!flipped) {
            return capacity - writePos;
        }
        return readPos - writePos;
    }

    /*
     * 不确保可以写入所有内容
     * flipped = true && writePos = readPos时数组满 无法插入
     */
    public boolean put(int element) {
        if (!flipped) {
            if (writePos == capacity) {
                //  数组已经写满 查看已读取过的空间
                writePos = 0;
                flipped = true;

                //  数组已写满 但是已读部分数据 读指针在写指针下方 写指针到读指针中间部分可以写入
                if (writePos < readPos) {
                    elements[writePos++] = element;
                    return true;
                } else {
                    //  读指针和写指针重合 数组写满 没有多余的空间写入
                    return false;
                }
            } else {
                //  还未写满
                elements[writePos++] = element;
                return true;
            }
        } else {
            //  数组已写满 但是已读部分数据 读指针在写指针下方 写指针到读指针中间部分可以写入
            if (writePos < readPos) {
                elements[writePos++] = element;
                return true;
            } else {
                return false;
            }
        }
    }

    //  返回写入的内容长度 不保证所有的内容都被写入
    public int put(int[] newElements, int length) {
        int newElementsReadPos = 0;
        if (!flipped) {
            // 写指针下方的数组都可用 且长度大于数组长度
            if (length <= capacity - writePos) {
                for (; newElementsReadPos < length; ++newElementsReadPos) {
                    this.elements[this.writePos++] = newElements[newElementsReadPos];
                }
                return newElementsReadPos;
            } else {
                //  先填充写指针到容量这部分未使用过的部分
                for (; this.writePos < capacity; ++this.writePos) {
                    this.elements[this.writePos] = newElements[newElementsReadPos++];
                }
                //  查看已读的部分
                this.writePos = 0;
                this.flipped = true;
                //  计算剩余还能够写入的内容长度
                int endPos = Math.min(this.readPos, length - newElementsReadPos);
                //  填充已读过的部分
                for (; this.writePos < endPos; ++this.writePos) {
                    this.elements[writePos] = newElements[newElementsReadPos++];
                }
                return newElementsReadPos;
            }
        } else {
            //  写指针在读指针的上方
            //  计算剩余还能够写入的内容长度
            int endPos = Math.min(this.readPos, this.writePos + length);
            for (; this.writePos < endPos; ++this.writePos) {
                this.elements[this.writePos] = newElements[newElementsReadPos++];
            }
            return newElementsReadPos;
        }
    }

    /*
     * flipped = false && writePos = readPos时数组空 无内容可读
     */
    public int take() {
        if (!flipped) {
            if (readPos < writePos) {
                return elements[readPos++];
            } else {
                //  数组中的内容为空
                return -1;
            }
        } else {
            if (readPos == capacity) {
                //  写指针在读指针上方 读指针跳回0点读取
                readPos = 0;
                flipped = false;
                if (readPos < writePos) {
                    return elements[readPos++];
                } else {
                    return -1;
                }
            } else {
                return elements[readPos++];
            }
        }
    }

    public int take(int[] into, int length) {
        int intoWritePos = 0;
        if (!flipped) {
            int endPos = Math.min(this.writePos, this.readPos + length);
            for (; this.readPos < endPos; ++this.readPos) {
                into[intoWritePos++] = this.elements[this.readPos];
            }
            return intoWritePos;
        } else {
            if (length < capacity - readPos) {
                for (; intoWritePos < length; ++intoWritePos) {
                    into[intoWritePos] = this.elements[this.readPos++];
                }
                return intoWritePos;
            } else {
                for (; this.readPos < capacity; ++this.readPos) {
                    into[intoWritePos++] = this.elements[this.readPos];
                }
                this.readPos = 0;
                this.flipped = false;
                int endPos = Math.min(this.writePos, length - intoWritePos);
                for (; this.readPos < endPos; ++this.readPos) {
                    into[intoWritePos++] = this.elements[this.readPos];
                }
                return intoWritePos;
            }
        }
    }

    public int size() {
        return capacity;
    }
}
