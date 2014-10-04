package com.oritsh.imageIO.codec.gdcm;

/**
 * Created by zarra on 14-10-3.
 */
public class Decodec {
    private long _npointer = -1;

    private GdcmDecodec codec;

    native private long _ninitCodec(int codec_index);

    native private void _nclose(long _npointer);

    native private boolean _ngetHeaderInfo(long _npointer,byte[] data);

    native private int _ngetWidth(long _npointer);

    native private int _ngetHeight(long _npointer);

    native private int _ngetBitsAllocated(long _npointer);

    native private int _ngetSamplesPerPixel(long _npointer);

    native private byte[] _ndecode(long _npointer,byte[] inputData);

    private long init(GdcmDecodec codec){
        return _ninitCodec(codec.ordinal());
    }

    public enum GdcmDecodec{
        JPEGDECODEC ,
        JPEG2KDECODEC,
        JPEGLSDECODEC
    };

    public Decodec(GdcmDecodec codec){
        this.codec = codec;
        this._npointer = init(this.codec);
    }

    public void dispose(){
        if (_npointer != -1) {
            this._nclose(_npointer);
            _npointer = -1;
        }
    }

    public boolean getHeaderInfo(byte[] data){
        return _ngetHeaderInfo(_npointer,data);
    }

    public int getWidth(){
        return  _ngetWidth(_npointer);
    }

    public int getHeight(){
        return _ngetHeight(_npointer);
    }

    public int getBitsAllocated(){
        return _ngetBitsAllocated(_npointer);
    }

    public int getSamplesPerPixel(){
        return _ngetSamplesPerPixel(_npointer);
    }

    public byte[] decode(byte[] input){
        return _ndecode(_npointer,input);
    }

    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }
}
