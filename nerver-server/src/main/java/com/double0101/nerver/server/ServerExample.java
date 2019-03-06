package com.double0101.nerver.server;

import com.double0101.nerver.core.IMessageProcessor;
import com.double0101.nerver.core.Message;
import com.double0101.nerver.core.Server;
import com.double0101.nerver.core.http.HttpMessageReaderFactory;

import java.io.IOException;

public class ServerExample {
    public static void main(String[] args) throws IOException {

        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 38\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body>Hello World!</body></html>";

        byte[] httpResponseBytes = httpResponse.getBytes("UTF-8");

        IMessageProcessor messageProcessor = (request, writeProxy) -> {
            System.out.println("Message Received from socket: " + request.socketId);

            Message response = writeProxy.getMessage();
            response.socketId = request.socketId;
            response.writeToMessage(httpResponseBytes);

            writeProxy.enqueue(response);
        };

        Server server = new Server(9999, new HttpMessageReaderFactory(), messageProcessor);

        server.start();

    }
}
