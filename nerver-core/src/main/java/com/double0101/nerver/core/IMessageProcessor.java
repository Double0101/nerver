package com.double0101.nerver.core;

public interface IMessageProcessor {

    public void process(Message message, WriteProxy writeProxy);
}
