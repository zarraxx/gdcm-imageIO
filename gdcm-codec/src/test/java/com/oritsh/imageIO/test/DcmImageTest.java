package com.oritsh.imageIO.test;

import com.oritsh.imageIO.codec.GdcmJPEG2KImageReaderSpi;
import com.oritsh.imageIO.codec.GdcmJPEGImageReaderSpi;
import com.oritsh.imageIO.codec.GdcmJPEGLSImageReaderSpi;
import org.dcm4che.data.Attributes;
import org.dcm4che.data.Tag;
import org.dcm4che.imageio.codec.ImageReaderFactory;
import org.dcm4che.io.DicomInputStream;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static com.oritsh.imageIO.test.TestHelper.getResource;

/**
 * Created with IntelliJ IDEA.
 * User: zarra
 * Date: 13-11-12
 * Time: 下午12:48
 * Copyright Shanghai Orient Rain Information Technology Co.,Ltd.
 */
public class DcmImageTest {

    public static void showImage(BufferedImage image){
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
    }

    @Before
    public void setup() throws IOException {

        GdcmJPEGImageReaderSpi spi = new GdcmJPEGImageReaderSpi();
        GdcmJPEG2KImageReaderSpi spi2k = new GdcmJPEG2KImageReaderSpi();
        GdcmJPEGLSImageReaderSpi spils = new GdcmJPEGLSImageReaderSpi();

        IIORegistry reg = IIORegistry.getDefaultInstance();

        reg.registerServiceProvider(spi);
        reg.registerServiceProvider(spi2k);
        reg.registerServiceProvider(spils);

        InputStream in  = getResource("ImageReaderFactory.properties");
        ImageReaderFactory readerFactory = ImageReaderFactory.getDefault();
        readerFactory.load(in);
        ImageReaderFactory.setDefault(readerFactory);

     //   Iterator spIt = reg.getServiceProviders(ImageReaderSpi.class, false);

//        while(spIt.hasNext()){
//            ImageReaderSpi it = (ImageReaderSpi) spIt.next();
//            System.out.println(""+it.getVendorName()+" | " + it.getVersion() + " | "+it.getDescription(null));
//
//        }
    }

    @Test
    public void run1() throws IOException {


        InputStream dcm = getResource("jpeg.dcm");
        //ImageInputStream iis = ImageIO.createImageInputStream(dcm);

        BufferedImage image = ImageIO.read(dcm);

        //showImage(image);

        //ImageIO.write(image,"png",new File("jpeg.png"));
    }

    @Test
    public void run2() throws IOException {

        InputStream dcm = getResource("jpeg2k.dcm");
        //ImageInputStream iis = ImageIO.createImageInputStream(dcm);

        BufferedImage image = ImageIO.read(dcm);

        //ImageIO.write(image,"png",new File("jpeg2k.png"));
    }

    @Test
    public void run3() throws IOException {

        InputStream dcm = getResource("jpegls.dcm");
        //ImageInputStream iis = ImageIO.createImageInputStream(dcm);

        BufferedImage image = ImageIO.read(dcm);

        //ImageIO.write(image,"png",new File("jpegls.png"));
    }

}
