package com.double0101.nerver.core.http;

import java.io.UnsupportedEncodingException;

public class HttpUtil {

    private static final byte[] GET    = new byte[]{'G','E','T'};
    private static final byte[] POST   = new byte[]{'P','O','S','T'};
    private static final byte[] PUT    = new byte[]{'P','U','T'};
    private static final byte[] HEAD   = new byte[]{'H','E','A','D'};
    private static final byte[] DELETE = new byte[]{'D','E','L','E','T','E'};

    private static final byte[] HOST           = new byte[]{'H','o','s','t'};
    private static final byte[] CONTENT_LENGTH = new byte[]{'C','o','n','t','e','n','t','-','L','e','n','g','t','h'};

    /*
     * 将请求信息放入HttpHeaders（头部信息+body开头结尾）
     */
    public static int parseHttpRequest(byte[] src, int startIndex, int endIndex, HttpHeaders httpHeaders) {

        int endOfFirstLine = findNextLineBreak(src, startIndex, endIndex);
        if (endOfFirstLine == -1) return -1;

        int prevEndOfHeader = endOfFirstLine + 1;
        int endOfHeader = findNextLineBreak(src, prevEndOfHeader, endIndex);

        while (endOfHeader != -1 && endOfHeader != prevEndOfHeader + 1) {
            if (matches(src, prevEndOfHeader, CONTENT_LENGTH)) {
                try {
                    findContentLength(src, prevEndOfHeader, endIndex, httpHeaders);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            prevEndOfHeader = endOfHeader + 1;
            endOfHeader = findNextLineBreak(src, prevEndOfHeader, endIndex);
        }
        if (endOfHeader == -1) {
            return -1;
        }

        int bodyStartIndex = endOfHeader + 1;
        int bodyEndIndex = bodyStartIndex + httpHeaders.contentLength;

        if (bodyEndIndex <= endIndex) {
            httpHeaders.bodyStartIndex = bodyStartIndex;
            httpHeaders.bodyEndIndex = bodyEndIndex;
            return bodyEndIndex;
        }

        return -1;
    }

    private static void findContentLength(byte[] src, int startIndex, int endIndex,
                                          HttpHeaders httpHeaders) throws UnsupportedEncodingException {
        int indexOfColon = findNext(src, startIndex, endIndex, (byte) ':');

        int index = indexOfColon + 1;
        while (src[index] == ' ') {
            ++index;
        }

        int valueStartIndex = index;
        int valueEndIndex   = index;
        boolean endOfValueFound = false;

        while (index < endIndex && !endOfValueFound) {
            switch (src[index]) {
                case '0' : ;
                case '1' : ;
                case '2' : ;
                case '3' : ;
                case '4' : ;
                case '5' : ;
                case '6' : ;
                case '7' : ;
                case '8' : ;
                case '9' : { index++;  break; }

                default: {
                    endOfValueFound = true;
                    valueEndIndex = index;
                }
            }
        }

        httpHeaders.contentLength = Integer.parseInt(new String(src, valueStartIndex, valueEndIndex - valueStartIndex, "UTF-8"));
    }

    public static int findNext(byte[] src, int startIndex, int endIndex, byte value) {
        for(int index = startIndex; index < endIndex; index++){
            if(src[index] == value) return index;
        }
        return -1;
    }

    /*
     * http请求报文中请求行与请求头部请求数据用\r\n隔开
     */
    public static int findNextLineBreak(byte[] src, int startIndex, int endIndex) {
        for(int index = startIndex; index < endIndex; ++index){
            if(src[index] == '\n'){
                if(src[index - 1] == '\r'){
                    return index;
                }
            };
        }
        return -1;
    }

    public static void resolveHttpMethod(byte[] src, int startIndex, HttpHeaders httpHeaders) {
        if(matches(src, startIndex, GET)) {
            httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_GET;
            return;
        }
        if(matches(src, startIndex, POST)){
            httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_POST;
            return;
        }
        if(matches(src, startIndex, PUT)){
            httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_PUT;
            return;
        }
        if(matches(src, startIndex, HEAD)){
            httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_HEAD;
            return;
        }
        if(matches(src, startIndex, DELETE)){
            httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_DELETE;
            return;
        }
    }

    public static boolean matches(byte[] src, int offset, byte[] value) {
        for (int i = offset, n = 0; n < value.length; ++i, ++n) {
            if (src[i] != value[n]) return false;
        }
        return true;
    }
}
