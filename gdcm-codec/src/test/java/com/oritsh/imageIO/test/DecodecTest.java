package com.oritsh.imageIO.test;

import com.oritsh.imageIO.codec.GdcmCodecFactory;
import com.oritsh.imageIO.codec.gdcm.Decodec;
import com.oritsh.imageIO.codec.gdcm.ImageCodecFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zarra on 14-10-4.
 */
public class DecodecTest {

    //@Test
    public  void run1() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("jplossless.raw");
        int bufferSize = inputStream.available();
        byte[] buffer = new byte[bufferSize];
        inputStream.read(buffer);


        Decodec decodec = GdcmCodecFactory.decodec(ImageCodecFactory.Codec.JPEGDECODEC);

        boolean b = decodec.getHeaderInfo(buffer);

        Assert.assertTrue(b);


        System.out.println("width:"+decodec.getWidth());
        System.out.println("height:"+decodec.getHeight());
        System.out.println("BitsAllocated:"+decodec.getBitsAllocated());
        System.out.println("SamplesPerPixel:"+decodec.getSamplesPerPixel());

        byte[] uncompress = decodec.decode(buffer);

        System.out.println("uncompress length:"+uncompress.length);

        decodec.dispose();
    }

    @Test
    public  void run2() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("jpegls.raw");
        int bufferSize = inputStream.available();
        byte[] buffer = new byte[bufferSize];
        inputStream.read(buffer);


        Decodec decodec = GdcmCodecFactory.decodec(ImageCodecFactory.Codec.JPEGLSDECODEC);

        boolean b = decodec.getHeaderInfo(buffer);

        Assert.assertTrue(b);


        System.out.println("width:"+decodec.getWidth());
        System.out.println("height:"+decodec.getHeight());
        System.out.println("BitsAllocated:"+decodec.getBitsAllocated());
        System.out.println("SamplesPerPixel:"+decodec.getSamplesPerPixel());

        byte[] uncompress = decodec.decode(buffer);

        System.out.println("uncompress length:"+uncompress.length);

        decodec.dispose();
    }

    @Test
    public  void run3() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("jpeg2k.jp2");
        int bufferSize = inputStream.available();
        byte[] buffer = new byte[bufferSize];
        inputStream.read(buffer);


        Decodec decodec = GdcmCodecFactory.decodec(ImageCodecFactory.Codec.JPEG2KDECODEC);

        boolean b = decodec.getHeaderInfo(buffer);

        Assert.assertTrue(b);


        System.out.println("width:"+decodec.getWidth());
        System.out.println("height:"+decodec.getHeight());
        System.out.println("BitsAllocated:"+decodec.getBitsAllocated());
        System.out.println("SamplesPerPixel:"+decodec.getSamplesPerPixel());

        byte[] uncompress = decodec.decode(buffer);

        System.out.println("uncompress length:"+uncompress.length);

        decodec.dispose();
    }
}
