package com.oritsh.imageIO.codec;

import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by zarra on 2014-10-06.
 */
public final class ImageOutputStreamAdapter extends OutputStream {

    ImageOutputStream stream;

    public ImageOutputStreamAdapter(ImageOutputStream stream) {
        super();

        this.stream = stream;
    }

    public void close() throws IOException {
        stream.close();
    }

    public void write(byte[] b) throws IOException {
        stream.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        stream.write(b, off, len);
    }

    public void write(int b) throws IOException {
        stream.write(b);
    }
}