package com.double0101.nerver.core.http;

import com.double0101.nerver.core.IMessageReader;
import com.double0101.nerver.core.Message;
import com.double0101.nerver.core.MessageBuffer;
import com.double0101.nerver.core.Socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class HttpMessageReader implements IMessageReader {

    private MessageBuffer messageBuffer = null;

    private List<Message> completeMessages = new ArrayList<Message>();
    private Message nextMessage = null;

    public HttpMessageReader() { }

    @Override
    public void init(MessageBuffer readMessageBuffer) {
        this.messageBuffer = readMessageBuffer;
        this.nextMessage = messageBuffer.getMessage();
        this.nextMessage.metaData = new HttpHeaders();
    }

    @Override
    public void read(Socket socket, ByteBuffer byteBuffer) throws IOException {
        int bytesRead = socket.read(byteBuffer);
        byteBuffer.flip();

        if (byteBuffer.remaining() == 0) {
            byteBuffer.clear();
            return;
        }
        this.nextMessage.writeToMessage(byteBuffer);
        int endIndex = HttpUtil.parseHttpRequest(this.nextMessage.sharedArray,
                this.nextMessage.offset,
                this.nextMessage.offset + this.nextMessage.length,
                (HttpHeaders) this.nextMessage.metaData);
        if (endIndex != -1) {
            Message message = this.messageBuffer.getMessage();
            message.metaData = new HttpHeaders();

            message.writePartialMessageToMessage(nextMessage, endIndex);

            completeMessages.add(nextMessage);
            nextMessage = message;
        }
        byteBuffer.clear();
    }

    @Override
    public List<Message> getMessages() {
        return this.completeMessages;
    }
}
