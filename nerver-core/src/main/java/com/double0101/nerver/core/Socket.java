package com.double0101.nerver.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Socket {

    public long socketId;

    public SocketChannel socketChannel = null;
    public IMessageReader messageReader = null;
    public MessageWriter messageWriter = null;

    public boolean endOfStreamReached = false;

    public Socket() { }

    public Socket(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /*
     * 将socketChannel中的数据读取到byteBuffer
     * 不保证全部读取
     * 返回读取的字节数
     */
    public int read(ByteBuffer byteBuffer) throws IOException {
        int bytesRead = this.socketChannel.read(byteBuffer);
        int totalBytesRead = bytesRead;

        while (bytesRead > 0) {
            bytesRead = this.socketChannel.read(byteBuffer);
            totalBytesRead += bytesRead;
        }
        if (bytesRead == -1) {
            this.endOfStreamReached = true;
        }

        return  totalBytesRead;
    }

    /*
     * 将byteBuffer中的数据写入socketChannel
     */
    public int write(ByteBuffer byteBuffer) throws IOException {
        int bytesWritten = this.socketChannel.write(byteBuffer);
        int totalBytesWritten = bytesWritten;
        while (bytesWritten > 0 && byteBuffer.hasRemaining()) {
            bytesWritten = this.socketChannel.write(byteBuffer);
            totalBytesWritten += bytesWritten;
        }

        return totalBytesWritten;
    }

}
