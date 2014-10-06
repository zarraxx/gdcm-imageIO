package com.oritsh.imageIO.test;

import com.oritsh.imageIO.codec.GdcmCodecFactory;
import com.oritsh.imageIO.codec.GdcmImageWriteParam;
import com.oritsh.imageIO.codec.GdcmImageWriter;
import com.oritsh.imageIO.codec.GdcmImageWriterSpi;
import com.oritsh.nativeUtils.NativeUtils;
import org.junit.Test;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.oritsh.imageIO.test.TestHelper.getResource;

/**
 * Created by zarra on 2014-10-06.
 */
public class ImageCompressTest {

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
    public BufferedImage getImage() throws IOException {
        InputStream dcm = getResource("decode.dcm");
        BufferedImage image = ImageIO.read(dcm);
        return image;
    }

    public void compressImage(RenderedImage image,File file,String compression_type) throws IOException {
        GdcmImageWriterSpi spi = new GdcmImageWriterSpi();
        GdcmImageWriter writer = (GdcmImageWriter) spi.createWriterInstance(null);
        GdcmImageWriteParam param = (GdcmImageWriteParam) writer.getDefaultWriteParam();
        param.setCompressionType(compression_type);
        param.setCompressionQuality(1.0f);
        ImageOutputStream ios = new FileImageOutputStream(file);
        writer.setOutput(ios);
        IIOImage iioImage = new IIOImage(image,null,null);
        writer.write(null,iioImage,param);
    }

    @Test
    public void run1() throws IOException {
        compressImage(getImage(),new File("compress.jp2"),GdcmImageWriteParam.JPEG2K_COMPRESSION_TYPE);
    }

    @Test
    public void run2() throws IOException {
        compressImage(getImage(),new File("compress.jpeg"),GdcmImageWriteParam.LOSSLESS_COMPRESSION_TYPE);
    }

    @Test
    public void run3() throws IOException {
        compressImage(getImage(),new File("compress.jpls"),GdcmImageWriteParam.LS_COMPRESSION_TYPE);
    }

    @Test
    public void run4() throws IOException {
        compressImage(getImage(),new File("compress.jpg"),GdcmImageWriteParam.LOSSY_COMPRESSION_TYPE);
    }
}
