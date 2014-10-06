package com.oritsh.imageIO.codec;

import com.oritsh.imageIO.codec.gdcm.*;

import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by zarra on 2014-10-06.
 */
public class GdcmImageWriter extends ImageWriter {

    protected ImageCodec getCodec(ImageWriteParam param){
        String ct = param.getCompressionType();
        ImageCodec codec = null;
        JpegCodec jpeg = null;
        JpegLSCodec jpegls = null;
        Jpeg2KCodec jpeg2k = null;
        switch (ct){
            case GdcmImageWriteParam.LOSSY_COMPRESSION_TYPE:
                jpeg  = new JpegCodec();
                jpeg.SetLossless(false);
                jpeg.SetQuality(param.getCompressionQuality());
                codec = jpeg;
                break;
            case GdcmImageWriteParam.LOSSLESS_COMPRESSION_TYPE:
                jpeg  = new JpegCodec();
                jpeg.SetLossless(true);
                codec = jpeg;
                break;
            case GdcmImageWriteParam.LS_COMPRESSION_TYPE:
                jpegls = new JpegLSCodec();
                jpegls.SetLossless(true);
                codec = jpegls;
                break;
            case GdcmImageWriteParam.JPEG2K_COMPRESSION_TYPE:
                jpeg2k = new Jpeg2KCodec();
                codec = jpeg2k;
                break;
            default:
                break;
        }
        return codec;
    }

    protected PixelFormat getPixelFormat(ColorModel cm,SampleModel sm){
        int transferType = cm.getTransferType();
        int samples = sm.getNumBands();
       // int width = sm.getWidth();
       // int height =  sm.getHeight();
        int bitsAllocated = 8;

        //System.out.println(transferType);

        switch (transferType){
            case DataBuffer.TYPE_BYTE:
                bitsAllocated = 8;
                break;
            case DataBuffer.TYPE_SHORT:
            case DataBuffer.TYPE_USHORT:
                bitsAllocated = 16;
                break;
            case DataBuffer.TYPE_INT:
                bitsAllocated = 4;
                break;
            default:
                break;
        }

        PixelFormat pf = new PixelFormat();
        pf.setBitsAllocated(bitsAllocated);
        if (bitsAllocated == 16) {
           // pf.setBitsStored(12);
        }
        pf.setSamplesPerPixel(samples);
        return pf;
    }

    protected byte[] getImageData(RenderedImage image) throws IOException {

        int transferType = image.getColorModel().getTransferType();

        int sbp = 8;
        switch (transferType){
            case DataBuffer.TYPE_BYTE:
                sbp = 8;
                break;
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_SHORT:
                sbp=16;
                break;
            case DataBuffer.TYPE_INT:
                sbp=32;
                break;
            default:
                break;
        }


        Raster raster = image.getData();

        int width = image.getWidth();
        int height = image.getHeight();
        int bank = raster.getNumBands();
        int sBp = sbp/8;

        byte[] result = new byte[width*height*bank*sBp];
        int index = 0;
        int[] pixel = new int[4];
        for (int i=0;i<height;i++){
            for (int j=0;j<width;j++){
                raster.getPixel(j,i,pixel);
                for (int k=0;k<bank;k++){
                    int sample = pixel[k];
                    switch (transferType){
                        case DataBuffer.TYPE_BYTE:
                            result[index] = (byte)sample;
                            index++;
                            break;
                        case DataBuffer.TYPE_USHORT:
                        case DataBuffer.TYPE_SHORT:
                            //int high = (sample & 0xFF00) >> 8;
                            //int low = sample & 0xFF;
                            byte[] tmp = ByteBuffer.allocate(2).putShort((short)sample).array();
                            result[index] =tmp[1];
                            result[index+1] = tmp[0];
                            index+=2;
                            break;
                        case DataBuffer.TYPE_INT:
                            //TODO
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        //System.out.println("index:" + index);
        return result;
    }

    protected ImageCodec.PIType getPIType(ColorModel cm){
        ColorSpace cs = cm.getColorSpace();
        ImageCodec.PIType piType = null;
        int cst = cs.getType();
        switch (cst){
            case ColorSpace.CS_GRAY:
            case ColorSpace.TYPE_GRAY:
                piType = ImageCodec.PIType.MONOCHROME2;
                break;
            case ColorSpace.CS_sRGB:
            case ColorSpace.CS_LINEAR_RGB:
                piType = ImageCodec.PIType.RGB;
                break;
            case ColorSpace.TYPE_CMYK:
                piType = ImageCodec.PIType.CMYK;
                break;
            case ColorSpace.TYPE_HSV:
                piType = ImageCodec.PIType.HSV;
                break;
            default:
                break;
        }
        return piType;
    }

    private static BufferedImage convertTo3BandRGB(RenderedImage im) {
        // Check parameter.
        if(im == null) {
            throw new IllegalArgumentException("im == null");
        }

        ColorModel cm = im.getColorModel();
        if(!(cm instanceof IndexColorModel)) {
            throw new IllegalArgumentException
                    ("!(im.getColorModel() instanceof IndexColorModel)");
        }

        Raster src;
        if(im.getNumXTiles() == 1 && im.getNumYTiles() == 1) {
            // Image is not tiled so just get a reference to the tile.
            src = im.getTile(im.getMinTileX(), im.getMinTileY());

            if (src.getWidth() != im.getWidth() ||
                    src.getHeight() != im.getHeight()) {
                src = src.createChild(src.getMinX(), src.getMinY(),
                        im.getWidth(), im.getHeight(),
                        src.getMinX(), src.getMinY(),
                        null);
            }
        } else {
            // Image is tiled so need to get a contiguous raster.
            src = im.getData();
        }

        // This is probably not the most efficient approach given that
        // the mediaLibImage will eventually need to be in component form.
        BufferedImage dst =
                ((IndexColorModel)cm).convertToIntDiscrete(src, false);

        if(dst.getSampleModel().getNumBands() == 4) {
            //
            // Without copying data create a BufferedImage which has
            // only the RGB bands, not the alpha band.
            //
            WritableRaster rgbaRas = dst.getRaster();
            WritableRaster rgbRas =
                    rgbaRas.createWritableChild(0, 0,
                            dst.getWidth(), dst.getHeight(),
                            0, 0,
                            new int[] {0, 1, 2});
            PackedColorModel pcm = (PackedColorModel)dst.getColorModel();
            int bits =
                    pcm.getComponentSize(0) +
                            pcm.getComponentSize(1) +
                            pcm.getComponentSize(2);
            DirectColorModel dcm = new DirectColorModel(bits,
                    pcm.getMask(0),
                    pcm.getMask(1),
                    pcm.getMask(2));
            dst = new BufferedImage(dcm, rgbRas, false, null);
        }

        return dst;
    }

    protected GdcmImageWriter(ImageWriterSpi originatingProvider) {
        super(originatingProvider);
    }

    @Override
    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return null;
    }

    @Override
    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
        return null;
    }

    @Override
    public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
        return null;
    }

    @Override
    public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
        return null;
    }

    @Override
    public ImageWriteParam getDefaultWriteParam() {
        return new GdcmImageWriteParam();
    }



    @Override
    public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
        OutputStream stream = null;
        if(output instanceof ImageOutputStream) {
            stream = new ImageOutputStreamAdapter((ImageOutputStream)output);
        } else {
            throw new IllegalArgumentException
                    ("!(output instanceof ImageOutputStream)");
        }
        RenderedImage renderedImage = image.getRenderedImage();
        if(renderedImage.getColorModel() instanceof IndexColorModel) {
            renderedImage = convertTo3BandRGB(renderedImage);
        }
        ColorModel cm = renderedImage.getColorModel();
        SampleModel sm = renderedImage.getSampleModel();

        int width = sm.getWidth();
        int height = sm.getHeight();
        int bitDepth = cm.getComponentSize(0);

        ImageCodec codec = getCodec(param);

        codec.SetNumberOfDimensions(2);
        codec.SetDimensions(new int[]{width,height,1});
        codec.SetPlanarConfiguration(0);
        codec.SetPhotometricInterpretation(getPIType(cm));

        PixelFormat pf = getPixelFormat(cm,sm);
        //pf.setScalarType(PixelFormat.ScalarType.INT16);
        codec.SetPixelFormat(pf);
        //pf.dispose();

        byte[] uncompress = getImageData(renderedImage);

        //System.out.println("width:"+width);
        //System.out.println("height:"+height);
        //System.out.println("length:"+uncompress.length);

        byte[] compress = codec.code(getImageData(renderedImage));

        //System.out.println(compress.length);
        //BufferedOutputStream out = new BufferedOutputStream(stream);
        stream.write(compress);
        //out.flush();
    }
}
