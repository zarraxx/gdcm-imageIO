package com.oritsh.imageIO.codec.gdcm;

/**
 * Created by zarra on 14-10-5.
 */
public class JpegLSCodec extends ImageCodec {
    @Override
    protected long newCodec() {
        return ImageCodecFactory.createImageCodec(ImageCodecFactory.Codec.JPEGLSDECODEC);
    }

    private native boolean _nGetLossless();
    private native void    _nSetLossless(boolean b);


    public boolean GetLossless(){
        checkHandle();
        return _nGetLossless();
    }
    public void    SetLossless(boolean b){
        checkHandle();
        _nSetLossless(b);
    }

}
