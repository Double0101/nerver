package com.double0101.nerver.core;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Server {

    private SocketAccepter socketAccepter = null;
    private SocketProcessor socketProcessor = null;

    private int tcpPort = 0;
    private IMessageReaderFactory messageReaderFactory = null;
    private IMessageProcessor messageProcessor = null;

    public Server(int tcpPort, IMessageReaderFactory messageReaderFactory, IMessageProcessor messageProcessor) {
        this.tcpPort = tcpPort;
        this.messageReaderFactory = messageReaderFactory;
        this.messageProcessor = messageProcessor;
    }

    public void start() throws IOException {

        Queue socketQueue = new ArrayBlockingQueue(1024);

        this.socketAccepter = new SocketAccepter(tcpPort, socketQueue);

        MessageBuffer readBuffer = new MessageBuffer();
        MessageBuffer writeBuffer = new MessageBuffer();

        this.socketProcessor = new SocketProcessor(socketQueue, readBuffer, writeBuffer,
                this.messageReaderFactory, this.messageProcessor);

        Thread acceptThread = new Thread(this.socketAccepter);
        Thread processorThread = new Thread(this.socketProcessor);

        acceptThread.start();
        processorThread.start();
    }
}
