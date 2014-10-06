package com.oritsh.imageIO.codec.gdcm;

/**
 * Created by zarra on 14-10-3.
 */
@Deprecated
public class Decodec {

    private ImageCodec imageCodec;

    public Decodec(ImageCodecFactory.Codec codec){
        switch (codec){
            case JPEGDECODEC:
                imageCodec = new JpegCodec();
                break;
            case JPEGLSDECODEC:
                imageCodec = new JpegLSCodec();
                break;
            case JPEG2KDECODEC:
                imageCodec = new Jpeg2KCodec();
                break;
            default:
                break;
        }
    }

    public void dispose(){
        imageCodec.dispose();
    }

    public boolean getHeaderInfo(byte[] data){
        PixelFormat pf = new PixelFormat();
        pf.setScalarType(PixelFormat.ScalarType.INT8);
        pf.setBitsAllocated(8);
        pf.setSamplesPerPixel(1);
        imageCodec.SetNumberOfDimensions(2);
        imageCodec.SetPixelFormat(pf);
        String s = imageCodec.GetHeaderInfo(data);
        return s!=null;
    }

    public int getWidth(){
        int w =  imageCodec.GetDimensions()[0];
        return w;
    }

    public int getHeight(){
        return imageCodec.GetDimensions()[1];
    }

    public int getBitsAllocated(){
        return imageCodec.GetPixelFormat().getBitsAllocated();
    }

    public int getSamplesPerPixel(){
        return imageCodec.GetPixelFormat().getSamplesPerPixel();
    }

    public byte[] decode(byte[] input){
        return imageCodec.decode(input);
    }

    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
}
