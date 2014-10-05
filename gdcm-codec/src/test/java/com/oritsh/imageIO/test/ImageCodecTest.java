package com.oritsh.imageIO.test;

import com.oritsh.imageIO.codec.GdcmCodecFactory;
import com.oritsh.imageIO.codec.gdcm.ImageCodec;
import com.oritsh.imageIO.codec.gdcm.JpegCodec;
import com.oritsh.imageIO.codec.gdcm.PixelFormat;
import com.oritsh.nativeUtils.NativeUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.oritsh.imageIO.test.TestHelper.getResource;

/**
 * Created by zarra on 14-10-5.
 */
public class ImageCodecTest {
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

    static int width = 2062;
    static int height = 1611;

    @Test
    public void run1(){
        JpegCodec codec = new JpegCodec();

        codec.CanCode(null);

        codec.SetNumberOfDimensions(2);
        codec.SetDimensions(new int[]{width,height});
        codec.SetPlanarConfiguration(0);
        codec.SetPhotometricInterpretation(ImageCodec.PIType.MONOCHROME2);

        PixelFormat pf = new PixelFormat();
        pf.setBitsAllocated(16);
        pf.setBitsStored(12);
        pf.setSamplesPerPixel(1);

        codec.SetPixelFormat(pf);

        pf.dispose();

        pf = null;

        pf = codec.GetPixelFormat();

        Assert.assertEquals(1,pf.getSamplesPerPixel());
        Assert.assertTrue(pf.isValid());

        int[] ds = codec.GetDimensions();

        Assert.assertEquals(0,codec.GetPlanarConfiguration());
        Assert.assertEquals(width,ds[0]);
        Assert.assertEquals(height,ds[1]);

        Assert.assertEquals(ImageCodec.PIType.MONOCHROME2,codec.GetPhotometricInterpretation());

        codec.GetHeaderInfo(new byte[]{0,0});
        codec.decode(new byte[]{0,0,0,0});
        codec.dispose();
    }

    @Test
    public void run2() throws IOException {
        InputStream raw = getResource("jplossless.raw");
        JpegCodec codec = new JpegCodec();
        PixelFormat pf = new PixelFormat();
        pf.setBitsAllocated(16);
        pf.setBitsStored(12);
        pf.setSamplesPerPixel(1);

        codec.SetPixelFormat(pf);
        codec.SetNumberOfDimensions(2);
        byte[] bytes = TestHelper.inputStreamToBytes(raw);
        String tsuid = codec.GetHeaderInfo(bytes);
        System.out.println(tsuid);

        byte[] out = codec.decode(bytes);

        Assert.assertNotNull(out);

        Assert.assertEquals(width*height*2,out.length);

        pf.dispose();
        codec.dispose();

        codec = new JpegCodec();
        codec.SetLossless(true);
        codec.SetNumberOfDimensions(2);
        codec.SetDimensions(new int[]{width,height,1});
        codec.SetPlanarConfiguration(0);
        codec.SetPhotometricInterpretation(ImageCodec.PIType.MONOCHROME2);

        pf = new PixelFormat();
        pf.setBitsAllocated(16);
        pf.setBitsStored(12);
        pf.setSamplesPerPixel(1);

        codec.SetPixelFormat(pf);

        byte[] compress = codec.code(out);

        codec.dispose();
        pf.dispose();

        Assert.assertNotNull(compress);
        System.out.println(bytes.length);
        System.out.println(compress.length);

        codec = new JpegCodec();
        pf = new PixelFormat();
        pf.setBitsAllocated(16);
        pf.setBitsStored(12);
        pf.setSamplesPerPixel(1);

        codec.SetPixelFormat(pf);
        codec.SetNumberOfDimensions(2);

        tsuid = codec.GetHeaderInfo(compress);

        pf.dispose();

        pf = codec.GetPixelFormat();

        byte[] out2 = codec.decode(compress);

        Assert.assertEquals(out.length,out2.length);


    }
}
