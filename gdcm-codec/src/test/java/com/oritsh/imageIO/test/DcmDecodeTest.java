package com.oritsh.imageIO.test;

import com.oritsh.imageIO.codec.GdcmJPEG2KImageReaderSpi;
import com.oritsh.imageIO.codec.GdcmJPEGImageReaderSpi;
import com.oritsh.imageIO.codec.GdcmJPEGLSImageReaderSpi;
import org.dcm4che.data.Attributes;
import org.dcm4che.data.Tag;
import org.dcm4che.data.UID;
import org.dcm4che.data.VR;
import org.dcm4che.imageio.codec.Compressor;
import org.dcm4che.imageio.codec.Decompressor;
import org.dcm4che.imageio.codec.ImageReaderFactory;
import org.dcm4che.io.DicomEncodingOptions;
import org.dcm4che.io.DicomInputStream;
import org.dcm4che.io.DicomOutputStream;
import org.dcm4che.util.Property;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.oritsh.imageIO.test.TestHelper.getResource;

/**
 * Created by zarra on 14-10-5.
 */
public class DcmDecodeTest {


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
    }

    static public void saveDataset(Attributes dataset,Attributes fmi,String tsuid,OutputStream dest)
            throws IOException {
        if(fmi == null){
            fmi = dataset.createFileMetaInformation(tsuid);
        }
        try (DicomOutputStream dos = new DicomOutputStream(dest,tsuid)) {
            DicomEncodingOptions encOpts = DicomEncodingOptions.DEFAULT;
            dos.setEncodingOptions(encOpts);
            dos.writeDataset(fmi, dataset);
            dos.flush();
            dos.close();
        }
    }

    static public void decode(DicomInputStream dis,String tsuid,OutputStream dest) throws IOException {
        if(tsuid==null){
            tsuid = UID.ExplicitVRLittleEndian;
        }

        Attributes fmi = dis.readFileMetaInformation();

        dis.setIncludeBulkData(DicomInputStream.IncludeBulkData.LOCATOR);

        Attributes dataset = dis.readDataset(-1, -1);
        Object pixeldata = dataset.getValue(Tag.PixelData);
        Decompressor.decompress(dataset, dis.getTransferSyntax());
        fmi.setString(Tag.TransferSyntaxUID, VR.UI, tsuid);

        saveDataset(dataset,fmi,tsuid,dest);
    }

    static public void encode(DicomInputStream dis,String tsuid,OutputStream dest) throws IOException {
        if(tsuid==null){
            tsuid = UID.JPEGLossless;
        }

        Attributes fmi = dis.readFileMetaInformation();

        dis.setIncludeBulkData(DicomInputStream.IncludeBulkData.LOCATOR);

        Attributes dataset = dis.readDataset(-1, -1);
        Object pixeldata = dataset.getValue(Tag.PixelData);

        List<Property> params = new ArrayList<Property>();

        Compressor compressor = new Compressor(dataset, dis.getTransferSyntax());

        compressor.compress(tsuid,
                params.toArray(new Property[params.size()]));

        fmi.setString(Tag.TransferSyntaxUID, VR.UI, tsuid);

        saveDataset(dataset,fmi,tsuid,dest);
    }

    @Test
    public void run1() throws IOException {
        InputStream dcm = getResource("jpeg.dcm");
        DicomInputStream dis = new DicomInputStream(dcm);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        decode(dis,null,outputStream);

        ByteArrayInputStream in = new ByteArrayInputStream(outputStream.toByteArray());
        BufferedImage image = ImageIO.read(in);
        DcmImageTest.showImage(image);
    }

    @Test
    public void run2() throws IOException {
        InputStream dcm = getResource("jpeg2k.dcm");
        DicomInputStream dis = new DicomInputStream(dcm);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        decode(dis,null,outputStream);

        ByteArrayInputStream in = new ByteArrayInputStream(outputStream.toByteArray());
        BufferedImage image = ImageIO.read(in);
        DcmImageTest.showImage(image);
    }
}
