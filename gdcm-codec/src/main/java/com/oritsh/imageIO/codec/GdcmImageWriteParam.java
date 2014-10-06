package com.oritsh.imageIO.codec;

import javax.imageio.ImageWriteParam;

/**
 * Created by zarra on 2014-10-06.
 */
public class GdcmImageWriteParam extends ImageWriteParam {
    private static final float DEFAULT_COMPRESSION_QUALITY = 0.75F;

    public static final String LOSSY_COMPRESSION_TYPE = "JPEG";
    public static final String LOSSLESS_COMPRESSION_TYPE = "JPEG-LOSSLESS";
    public static final String LS_COMPRESSION_TYPE = "JPEG-LS";
    public static final String JPEG2K_COMPRESSION_TYPE = "JPEG2000";

    public GdcmImageWriteParam() {
        canWriteCompressed = true;
        compressionMode = MODE_EXPLICIT;
        compressionQuality = DEFAULT_COMPRESSION_QUALITY;
        compressionType = LOSSLESS_COMPRESSION_TYPE;

        compressionTypes = new String[]{
                LOSSY_COMPRESSION_TYPE,
                LOSSLESS_COMPRESSION_TYPE,
                LS_COMPRESSION_TYPE,
                JPEG2K_COMPRESSION_TYPE
        };
    }

    public String[] getCompressionQualityDescriptions() {
        return new String[]{"Minimum useful","Visually lossless","Maximum useful"};
    }

    public float[] getCompressionQualityValues() {
        super.getCompressionQualityValues(); // Performs checks.

        return new float[] { 0.05F,   // "Minimum useful"
                0.75F,   // "Visually lossless"
                0.95F }; // "Maximum useful"
    }

    public boolean isCompressionLossless() {
        super.isCompressionLossless(); // Performs checks.

        return !compressionType.equalsIgnoreCase(LOSSY_COMPRESSION_TYPE);
    }

    public void setCompressionMode(int mode) {
        if(mode == MODE_DISABLED ||
                mode == MODE_COPY_FROM_METADATA) {
            throw new UnsupportedOperationException
                    ("mode == MODE_DISABLED || mode == MODE_COPY_FROM_METADATA");
        }

        super.setCompressionMode(mode); // This sets the instance variable.
    }

    public void unsetCompression() {
        super.unsetCompression(); // Performs checks.
        compressionQuality = DEFAULT_COMPRESSION_QUALITY;
        compressionType = LOSSLESS_COMPRESSION_TYPE;
    }
}
