package com.oritsh.imageIO.codec.gdcm;

/**
 * Created by zarra on 14-10-5.
 */
public class JpegCodec extends ImageCodec {
    @Override
    protected long newCodec() {
        return ImageCodecFactory.createImageCodec(ImageCodecFactory.Codec.JPEGDECODEC);
    }

    private native boolean _nGetLossless();
    private native void    _nSetLossless(boolean b);
    private native double  _nGetQuality();
    private native void    _nSetQuality(double q);


    public boolean GetLossless(){
        checkHandle();
        return _nGetLossless();
    }
    public void    SetLossless(boolean b){
        checkHandle();
        _nSetLossless(b);
    }
    public double  GetQuality(){
        checkHandle();
        return _nGetQuality();
    }
    public void    SetQuality(double q){
        checkHandle();
        _nSetQuality(q);
    }


}
