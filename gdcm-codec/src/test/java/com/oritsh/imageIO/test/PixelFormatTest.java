package com.oritsh.imageIO.test;

import com.oritsh.imageIO.codec.gdcm.NativeHandleException;
import com.oritsh.imageIO.codec.gdcm.PixelFormat;
import com.oritsh.nativeUtils.NativeUtils;
import org.junit.Assert;
import org.junit.Test;

import com.oritsh.imageIO.codec.GdcmCodecFactory;

import java.io.IOException;

/**
 * Created by zarra on 14-10-5.
 */
public class PixelFormatTest {
    static {
        try {
            System.loadLibrary("gdcmcodec");
        } catch (UnsatisfiedLinkError e) {
            try {
                NativeUtils.load("gdcmcodec", GdcmCodecFactory.class);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Test(expected = NativeHandleException.class)
    public void run1() {
        PixelFormat pixelFormat = new PixelFormat();
        pixelFormat.setBitsAllocated(16);
        Assert.assertEquals(pixelFormat.getBitsAllocated(), 16);

        pixelFormat.setScalarType(PixelFormat.ScalarType.INT16);
        Assert.assertEquals(pixelFormat.getScalarType(), PixelFormat.ScalarType.INT16);

        System.out.println("ScalarType:" + pixelFormat.getScalarTypeAsString());

        pixelFormat.dispose();

        System.out.println("ScalarType:" + pixelFormat.getScalarTypeAsString());


    }
}
