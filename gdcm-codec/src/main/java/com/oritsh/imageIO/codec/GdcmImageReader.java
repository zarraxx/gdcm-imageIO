package com.oritsh.imageIO.codec;

import com.oritsh.imageIO.codec.gdcm.Decodec;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * Created by zarra on 14-10-4.
 */
public abstract class GdcmImageReader extends ImageReader {

    private Decodec decodec;

    private ImageInputStream iis;

    private boolean hasInit = false;

    private GdcmMetaData metaData;

    private ColorModel cm;

    private SampleModel sm;

    private byte[] rawData;

    private void resetInternalState() {
        hasInit = false;
        rawData = null;
        metaData = null;
        cm = null;
        sm = null;

    }

    private void readMetadata() throws IOException {
        if (metaData == null)
            metaData = new GdcmMetaData();
        if (cm == null)
            prepareColorModel();
        if (sm == null)
            prepareSampleModel();
    }

    private void prepareColorModel() {
        if (decodec.getSamplesPerPixel() == 1) {
            cm = new ComponentColorModel(
                    ColorSpace.getInstance(ColorSpace.CS_GRAY),
                    new int[]{16},
                    false, // hasAlpha
                    false, // isAlphaPremultiplied
                    Transparency.OPAQUE,
                    DataBuffer.TYPE_USHORT);
        } else if (decodec.getSamplesPerPixel() == 3) {
            int bitPerSample = decodec.getBitsAllocated();
            cm = new ComponentColorModel(
                    ColorSpace.getInstance(ColorSpace.CS_sRGB),
                    new int[]{bitPerSample, bitPerSample, bitPerSample},
                    false, // hasAlpha
                    false, // isAlphaPremultiplied
                    Transparency.OPAQUE,
                    DataBuffer.TYPE_BYTE);
        } else {
            cm = null;
        }
    }

    private void prepareSampleModel() {
        int samples = decodec.getSamplesPerPixel();
        int sampleLengthByByte = (decodec.getBitsAllocated() + 7) / 8;
        int[] indicies = new int[samples];
        for (int i = 1; i < samples; i++)
            indicies[i] = i * sampleLengthByByte;
        int w = decodec.getWidth();
        int h = decodec.getHeight();

        if (samples == 3) {
            sm = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, w, h,
                    samples, w * samples, indicies);
        } else if (samples == 1) {

            sm = new PixelInterleavedSampleModel(DataBuffer.TYPE_USHORT, w, h,
                    samples, w * samples, indicies);
        } else {
            sm = null;
        }

    }

    /**
     * Constructs an <code>ImageReader</code> and sets its
     * <code>originatingProvider</code> field to the supplied value.
     * <p/>
     * <p> Subclasses that make use of extensions should provide a
     * constructor with signature <code>(ImageReaderSpi,
     * Object)</code> in order to retrieve the extension object.  If
     * the extension object is unsuitable, an
     * <code>IllegalArgumentException</code> should be thrown.
     *
     * @param originatingProvider the <code>ImageReaderSpi</code> that is
     *                            invoking this constructor, or <code>null</code>.
     */
    protected GdcmImageReader(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
        decodec = createDecodec();
    }

    protected abstract Decodec createDecodec();

    @Override
    public int getNumImages(boolean allowSearch) throws IOException {
        return 1;
    }

    @Override
    public int getWidth(int imageIndex) throws IOException {
        if (hasInit) {
            return decodec.getWidth();
        }
        return 0;
    }

    @Override
    public int getHeight(int imageIndex) throws IOException {
        if (hasInit) {
            return decodec.getHeight();
        }
        return 0;
    }

    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex) throws IOException {
        if (hasInit) {
            ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(cm, sm);
            return Collections.singletonList(imageTypeSpecifier).iterator();
        }
        return null;
    }

    @Override
    public IIOMetadata getStreamMetadata() throws IOException {
        if (hasInit) {
            return metaData;
        }
        return null;
    }

    @Override
    public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
        return null;
    }

    @Override
    public void dispose() {
        resetInternalState();
        decodec.dispose();
        super.dispose();
    }

    @Override
    public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
        if (hasInit) {
            WritableRaster raster = (WritableRaster) readRaster(imageIndex, null);
            BufferedImage image = new BufferedImage(cm, raster, false, null);
            return image;
        }
        return null;
    }

    @Override
    public boolean canReadRaster() {
        return true;
    }

    @Override
    public Raster readRaster(int frameIndex, ImageReadParam param) throws IOException {
        if (hasInit) {
//            iis.mark();
//            ImageInputStreamAdapter inputStreamAdapter = new ImageInputStreamAdapter(iis);
//            BufferedInputStream inputStream = new BufferedInputStream(inputStreamAdapter);
//            int cbufferSize = inputStream.available();
//            byte[] cbuffer = new byte[cbufferSize];
//            int read = inputStream.read(cbuffer);
//
//            System.out.println("read:"+read);
//            iis.reset();

            byte[] uncompress = decodec.decode(rawData);

            InputStream unin = new ByteArrayInputStream(uncompress);

            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(unin));

            DataBuffer buffer = sm.createDataBuffer();

            //int inputLength = dataInputStream.available();
//
            for (int i = 0; i < buffer.getSize(); i++) {
                if (decodec.getBitsAllocated() > 8) {
                    int v1 = dataInputStream.readUnsignedByte();
                    int v2 = dataInputStream.readUnsignedByte();
                    int high = v2 << 8 & 0xFFFF;
                    buffer.setElem(i, high + v1);
                } else {
                    int v1 = dataInputStream.readUnsignedByte();
                    //int v2 = dataInputStream.readUnsignedByte();
                    //int high = v2 << 8 & 0xFFFF;
                    buffer.setElem(i, v1);
                }
            }
            WritableRaster raster = Raster.createWritableRaster(sm, buffer, null);
            return raster;
        }
        return null;
    }


    @Override
    public void setInput(Object input, boolean seekForwardOnly,
                         boolean ignoreMetadata) {
        super.setInput(input, seekForwardOnly, ignoreMetadata);
        resetInternalState();

        try {
            iis = (ImageInputStream) input;
            iis.mark();

            ImageInputStreamAdapter inputStreamAdapter = new ImageInputStreamAdapter(iis);

            BufferedInputStream inputStream = new BufferedInputStream(inputStreamAdapter);

            int bufferSize = 8192;
            byte[] buffer = new byte[bufferSize];

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int readSize = 0;
            do {
                readSize = inputStreamAdapter.read(buffer);
                outputStream.write(buffer, 0, readSize);
            } while (readSize == bufferSize);

            outputStream.flush();



            rawData = outputStream.toByteArray();

            boolean b = decodec.getHeaderInfo(rawData);

            hasInit = b;

            if (hasInit) {
                readMetadata();
            }

            iis.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
