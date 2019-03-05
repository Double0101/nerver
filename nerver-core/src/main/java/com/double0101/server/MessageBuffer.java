package com.double0101.server;

public class MessageBuffer {

    public static final int KB = 1024;
    public static final int MB = 1024 * KB;

    private static final int CAPACITY_SMALL  =   4  * KB;
    private static final int CAPACITY_MEDIUM = 128  * KB;
    private static final int CAPACITY_LARGE  = 1024 * KB;

    byte[]  smallMessageBuffer  = new byte[1024 *   4 * KB];
    byte[]  mediumMessageBuffer = new byte[128  * 128 * KB];
    byte[]  largeMessageBuffer  = new byte[16   *   1 * MB];

}
