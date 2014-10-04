package com.oritsh.imageIO.codec;

import com.oritsh.imageIO.codec.gdcm.Decodec;

import javax.imageio.spi.ImageReaderSpi;

/**
 * Created by zarra on 14-10-4.
 */
public class GdcmJPEGLSImageReader extends GdcmImageReader {
    /**
     * Constructs an <code>ImageReader</code> and sets its
     * <code>originatingProvider</code> field to the supplied value.
     * <p/>
     * <p> Subclasses that make use of extensions should provide a
     * constructor with signature <code>(ImageReaderSpi,
     * Object)</code> in order to retrieve the extension object.  If
     * the extension object is unsuitable, an
     * <code>IllegalArgumentException</code> should be thrown.
     *
     * @param originatingProvider the <code>ImageReaderSpi</code> that is
     *                            invoking this constructor, or <code>null</code>.
     */
    protected GdcmJPEGLSImageReader(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
    }

    @Override
    protected Decodec createDecodec() {
         return GdcmCodecFactory.decodec(Decodec.GdcmDecodec.JPEGLSDECODEC);
    }
}
