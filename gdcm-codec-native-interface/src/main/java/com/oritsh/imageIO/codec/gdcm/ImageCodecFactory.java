package com.oritsh.imageIO.codec.gdcm;

/**
 * Created by zarra on 14-10-5.
 */
public class ImageCodecFactory {
    public enum Codec{
        JPEGDECODEC ,
        JPEG2KDECODEC,
        JPEGLSDECODEC
    };
    static private native long createImageCodec(int index);

    static public long createImageCodec(Codec codec){
        return createImageCodec(codec.ordinal());
    }
}
