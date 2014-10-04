package com.oritsh.imageIO.codec;

import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: zarra
 * Date: 13-11-11
 * Time: 下午7:12
 * * Copyright Shanghai Orient Rain Information Technology Co.,Ltd.
 */
public class ImageInputStreamAdapter extends InputStream {

    private final ImageInputStream iis;

    private long markedPos;

    private IOException markException;

    public static byte[] readImageInputStream(ImageInputStream iis) throws IOException {
        ImageInputStreamAdapter iisa = new ImageInputStreamAdapter(iis);
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int buffLen = 1024;
        byte[] buff = new byte[buffLen];
        int rc = 0;
        while ((rc = iisa.read(buff, 0, buffLen)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    public ImageInputStreamAdapter(ImageInputStream iis) {
        this.iis = iis;
    }

    @Override
    public int read() throws IOException {
        return iis.read();
    }

    @Override
    public synchronized void mark(int readlimit) {
        try {
            this.markedPos = iis.getStreamPosition();
            this.markException = null;
        } catch (IOException e) {
            this.markException = e;
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return iis.read(b, off, len);
    }

    @Override
    public synchronized void reset() throws IOException {
        if (markException != null)
            throw markException;
        iis.seek(markedPos);
    }

    @Override
    public long skip(long n) throws IOException {
        return iis.skipBytes((int) n);
    }

}