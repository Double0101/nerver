package com.double0101.nerver.core;

import java.util.Queue;

public class WriteProxy {
    private MessageBuffer messageBuffer = null;
    private Queue writeQueue = null;

    public WriteProxy(MessageBuffer messageBuffer, Queue queue) {
        this.messageBuffer = messageBuffer;
        this.writeQueue = queue;
    }

    public Message getMessage() {
        return this.messageBuffer.getMessage();
    }

    public boolean enqueue(Message message) {
        return this.writeQueue.offer(message);
    }
}
