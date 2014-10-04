package com.oritsh.imageIO.codec;

import javax.imageio.ImageReader;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by zarra on 14-10-4.
 */
public class GdcmJPEGImageReaderSpi extends GdcmImageReaderSpi {

    public GdcmJPEGImageReaderSpi() {
        super(GdcmJPEGImageReaderSpi.class);
    }

    @Override
    public ImageReader createReaderInstance(Object extension) throws IOException {
        return new GdcmJPEGImageReader(this);
    }

    @Override
    public String getDescription(Locale locale) {
        return "JPEG Image Reader base on GDCM";
    }
}
