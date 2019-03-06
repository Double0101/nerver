package com.double0101.nerver.core.http;

import com.double0101.nerver.core.IMessageReader;
import com.double0101.nerver.core.IMessageReaderFactory;

public class HttpMessageReaderFactory implements IMessageReaderFactory {

    public HttpMessageReaderFactory() {
    }

    @Override
    public IMessageReader createMessageReader() {
        return new HttpMessageReader();
    }
}
