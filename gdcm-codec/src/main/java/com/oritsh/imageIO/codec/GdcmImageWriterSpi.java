package com.oritsh.imageIO.codec;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by zarra on 2014-10-06.
 */
public class GdcmImageWriterSpi extends ImageWriterSpi {

    private static final String[] names =
            {"jpeg", "JPEG", "jpg", "JPG", "jfif", "JFIF",
                    "jpeg-lossless", "JPEG-LOSSLESS", "jpeg-ls", "JPEG-LS","jpeg 2000", "JPEG 2000", "jpeg2000", "JPEG2000"};

    private static final String[] suffixes = {"jpeg", "jpg", "jfif", "jls","jp2"};

    private static final String[] MIMETypes = { "image/jpeg","image/jp2", "image/jpeg2000" };

    private static final Class<?>[] outputTypes = {ImageOutputStream.class};

    private static final String writerClassName =
            GdcmImageWriter.class.getName();

    private static final String[] readerSpiNames = {
            GdcmJPEGImageReader.class.getName(),
            GdcmJPEG2KImageReader.class.getName(),
            GdcmJPEGLSImageReader.class.getName(),
    };

    public GdcmImageWriterSpi(){
        super( "com.oritsh.imageIO",
                "1.0",
                names,
                suffixes,
                MIMETypes,
                writerClassName,
                outputTypes,
                readerSpiNames,
                false,
                null, null,
                null, null,
                false,
                null, null,
                null, null);
    }

    @Override
    public boolean canEncodeImage(ImageTypeSpecifier type) {
        ColorModel colorModel = type.getColorModel();

        if (colorModel instanceof IndexColorModel) {
            // No need to check further: writer converts to 8-8-8 RGB.
            return true;
        }

        SampleModel sampleModel = type.getSampleModel();

        // Ensure all channels have the same bit depth
        int bitDepth;
        if(colorModel != null) {
            int[] componentSize = colorModel.getComponentSize();
            bitDepth = componentSize[0];
            for (int i = 1; i < componentSize.length; i++) {
                if (componentSize[i] != bitDepth) {
                    return false;
                }
            }
        } else {
            int[] sampleSize = sampleModel.getSampleSize();
            bitDepth = sampleSize[0];
            for (int i = 1; i < sampleSize.length; i++) {
                if (sampleSize[i] != bitDepth) {
                    return false;
                }
            }
        }

        // Ensure bitDepth is no more than 16
        if (bitDepth > 16) {
            return false;
        }

        // Check number of bands.
        int numBands = sampleModel.getNumBands();
        if (numBands < 1 || numBands > 4) {
            return false;
        }

        return true;
    }

    @Override
    public ImageWriter createWriterInstance(Object extension) throws IOException {
        return new GdcmImageWriter(this);
    }

    @Override
    public String getDescription(Locale locale) {
        return "JPEG;JPEGLS;JPEG2000 Image Writer base on GDCM";
    }
}
