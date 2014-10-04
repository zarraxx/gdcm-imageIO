package com.oritsh.imageIO.codec;

import com.oritsh.imageIO.codec.gdcm.Decodec;
import com.oritsh.nativeUtils.NativeUtils;

import java.io.IOException;

/**
 * Created by zarra on 14-10-4.
 */
public class GdcmCodecFactory {
    static{
        try{
            System.loadLibrary("gdcmcodec");
        }catch (UnsatisfiedLinkError e){
            try {
                NativeUtils.load("gdcmcodec",GdcmCodecFactory.class);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static Decodec decodec(Decodec.GdcmDecodec codec){
        Decodec decodec = new Decodec(codec);
        return decodec;
    }


}
