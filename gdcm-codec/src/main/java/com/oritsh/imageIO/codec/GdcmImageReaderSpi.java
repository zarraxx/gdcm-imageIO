package com.oritsh.imageIO.codec;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by zarra on 14-10-4.
 */
public abstract class GdcmImageReaderSpi extends ImageReaderSpi {

    private static final String vendorName = "com.oritsh.imageIO.codec";
    private static final String version = "1.0";
    private static final String[] formatNames = {"jpeg", "JPEG", "jpg", "JPG", "jfif", "JFIF",
            "jpeg-lossless", "JPEG-LOSSLESS", "jpeg-ls", "JPEG-LS"};
    private static final String[] suffixes = {"jpeg", "jpg", "jfif", "jls"};
    private static final String[] MIMETypes = {"image/jpeg"};
    private static final Class<?>[] inputTypes = {ImageInputStream.class};

    public GdcmImageReaderSpi(Class<? extends GdcmImageReaderSpi> clazz){
        super(getGdcmSpiVendorName(), getGdcmSpiVersion(), getGdcmSpiFormatNames(), getGdcmSpiSuffixes(), getGdcmSpiMIMETypes(),
                clazz.getName(), inputTypes,
                null, // writerSpiNames
                false, // supportsStandardStreamMetadataFormat
                null, // nativeStreamMetadataFormatName
                null, // nativeStreamMetadataFormatClassName
                null, // extraStreamMetadataFormatNames
                null, // extraStreamMetadataFormatClassNames
                false, // supportsStandardImageMetadataFormat
                null, // nativeImageMetadataFormatName
                null, // nativeImageMetadataFormatClassName
                null, // extraImageMetadataFormatNames
                null); // extraImageMetadataFormatClassNames

    }

    static protected String getGdcmSpiVersion(){
        return version;
    }

    static protected String getGdcmSpiVendorName(){
        return vendorName;
    }

    static protected String[] getGdcmSpiSuffixes(){
        return suffixes;
    }

    static protected String[] getGdcmSpiFormatNames(){
        return formatNames;
    }

    static protected String[] getGdcmSpiMIMETypes(){
        return MIMETypes;
    }

    @Override
    public boolean canDecodeInput(Object source) throws IOException {
        if (!(source instanceof ImageInputStream)) {
            return false;
        }
        ImageInputStream iis = (ImageInputStream) source;
        iis.mark();
// If the first two bytes are a JPEG SOI marker, it's probably
// a JPEG file. If they aren't, it definitely isn't a JPEG file.
        int byte1 = iis.read();
        int byte2 = iis.read();
        if ((byte1 != 0xFF) || (byte2 != 0xD8)) {
            iis.reset();
            return false;
        }
        do {
            byte1 = iis.read();
            byte2 = iis.read();
            if (byte1 != 0xFF) break; // something wrong, but probably readable
            if (byte2 == 0xDA) break; // Start of scan
            if (byte2 == 0xC2) { // progressive mode, can't decode
                iis.reset();
                return false;
            }
            if ((byte2 >= 0xC0) && (byte2 <= 0xC3)) // not progressive, can decode
                break;
            int length = iis.read() << 8;
            length += iis.read();
            length -= 2;
            while (length > 0) length -= iis.skipBytes(length);
        } while (true);
        iis.reset();
        return true;
    }

    public abstract ImageReader createReaderInstance(Object extension) throws IOException ;

    public abstract String getDescription(Locale locale) ;
}
