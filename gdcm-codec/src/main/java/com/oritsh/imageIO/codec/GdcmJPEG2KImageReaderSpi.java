package com.oritsh.imageIO.codec;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by zarra on 14-10-4.
 */
public class GdcmJPEG2KImageReaderSpi extends ImageReaderSpi {

    private static String[] formatNames =
            {"jpeg 2000", "JPEG 2000", "jpeg2000", "JPEG2000"};
    private static String[] extensions =
            {"jp2"}; // Should add jpx or jpm
    private static String[] mimeTypes = {"image/jp2", "image/jpeg2000"};
    private static final Class<?>[] inputTypes = {ImageInputStream.class};
    public GdcmJPEG2KImageReaderSpi() {
        super("com.oritsh.imageIO.codec",
                "1.0",
                formatNames,
                extensions,
                mimeTypes,
                GdcmJPEG2KImageReader.class.getName(),
                inputTypes,
                null,
                false,
                null, null,
                null, null,
                true,
                null,
                null,
                null, null);
    }

    @Override
    public boolean canDecodeInput(Object source) throws IOException {
        if (!(source instanceof ImageInputStream)) {
            return false;
        }

        ImageInputStream stream = (ImageInputStream)source;

        //fix of 4938421
        stream.mark();
        int marker = (stream.read() << 8) | stream.read();

        if (marker == 0xFF4F) {
            stream.reset();
            return true;
        }

        stream.reset();
        stream.mark();
        byte[] b = new byte[12];
        stream.readFully(b);
        stream.reset();

        //Verify the signature box

        // The length of the signature box is 12
        if (b[0] !=0 || b[1]!=0 || b[2] != 0 || b[3] != 12)
            return false;

        // The signature box type is "jP  "
        if ((b[4] & 0xff) != 0x6A || (b[5] & 0xFF) != 0x50 ||
                (b[6] & 0xFF) !=0x20 || (b[7] & 0xFF) != 0x20)
            return false;

        // The signture content is 0x0D0A870A
        if ((b[8] & 0xFF) != 0x0D || (b[9] & 0xFF) != 0x0A ||
                (b[10] & 0xFF) != 0x87 || (b[11] &0xFF) != 0x0A)
            return false;

        return true;
    }

    @Override
    public ImageReader createReaderInstance(Object extension) throws IOException {
        return new GdcmJPEG2KImageReader(this);
    }

    @Override
    public String getDescription(Locale locale) {
        return "JPEG2K Image Reader base on GDCM";
    }
}
