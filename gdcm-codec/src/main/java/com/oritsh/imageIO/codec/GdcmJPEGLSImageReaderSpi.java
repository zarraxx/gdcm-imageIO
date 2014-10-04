package com.oritsh.imageIO.codec;

import javax.imageio.ImageReader;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by zarra on 14-10-4.
 */
public class GdcmJPEGLSImageReaderSpi extends GdcmImageReaderSpi {
    public GdcmJPEGLSImageReaderSpi() {
        super(GdcmJPEGLSImageReaderSpi.class);
    }

    @Override
    public ImageReader createReaderInstance(Object extension) throws IOException {
        return new GdcmJPEGLSImageReader(this);
    }

    @Override
    public String getDescription(Locale locale) {
        return "JPEGLS Image Reader base on GDCM";
    }
}
