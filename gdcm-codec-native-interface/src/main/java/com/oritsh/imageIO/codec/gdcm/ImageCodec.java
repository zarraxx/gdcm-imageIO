package com.oritsh.imageIO.codec.gdcm;

/**
 * Created by zarra on 14-10-5.
 */
public abstract class ImageCodec {

    public enum PIType {
        UNKNOW ,
        MONOCHROME1,
        MONOCHROME2,
        PALETTE_COLOR,
        RGB,
        HSV,
        ARGB,
        CMYK,
        YBR_FULL,
        YBR_FULL_422,
        YBR_PARTIAL_422,
        YBR_PARTIAL_420,
        YBR_ICT,
        YBR_RCT,
        PI_END
    };

    private long _handle;

    private native void _ndelete();

    private native boolean _nCanCode(String tsuid);
    private native boolean _nCanDecode(String tsuid);
    private native int[]   _nGetDimensions();
    private native String _nGetHeaderInfo(byte[] istream);
    private native boolean _nGetLossyFlag();
    private native boolean _nGetNeedByteSwap();
    private native int     _nGetNumberOfDimensions();
    private native int     _nGetPhotometricInterpretation ();
    private native long    _nGetPixelFormat();
    private native int     _nGetPlanarConfiguration ();
    private native boolean _nIsLossy();

    private native void _nSetDimensions(int[] d);
    private native void _nSetLossyFlag(boolean l);
    private native void _nSetNeedByteSwap(boolean b);
    private native void _nSetNumberOfDimensions(int dim);
    private native void _nSetPhotometricInterpretation(int pi);
    private native void _nSetPixelFormat(long pfHandle);
    private native void _nSetPlanarConfiguration(int pc);

    private native byte[] _ndecode(byte[] input);
    private native byte[] _ncode(byte[] input);

    public ImageCodec(){
        _handle = newCodec();
    }

    public void dispose(){
        if (_handle!=-1){
            _ndelete();
            _handle = -1;
        }
    }

    protected abstract long newCodec();

    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    protected void checkHandle(){
        if (_handle == -1){
            throw  new NativeHandleException("native handle is destroy!");
        }
    }

    public boolean CanCode(String tsuid){
        checkHandle();
        if(tsuid == null)
            tsuid = "";
        return _nCanCode(tsuid);
    }
    public boolean CanDecode(String tsuid){
        checkHandle();
        if(tsuid == null)
            tsuid = "";
        return _nCanDecode(tsuid);
    }
    public int[]   GetDimensions(){
        checkHandle();
        return _nGetDimensions();
    }
    public String  GetHeaderInfo(byte[] istream){
        checkHandle();
        if (istream == null)
            throw new NullPointerException("istream must not be null");
        if(istream.length<2)
            return null;
        return _nGetHeaderInfo(istream);
    }
    public boolean GetLossyFlag(){
        checkHandle();
        return _nGetLossyFlag();
    }
    public boolean GetNeedByteSwap(){
        checkHandle();
        return _nGetNeedByteSwap();
    }
    public int     GetNumberOfDimensions(){
        checkHandle();
        return _nGetNumberOfDimensions();
    }
    public PIType     GetPhotometricInterpretation (){
        checkHandle();
        int index = _nGetPhotometricInterpretation();
        return PIType.values()[index];
    }
    public PixelFormat    GetPixelFormat(){
        checkHandle();
        long handle = _nGetPixelFormat();
        return new PixelFormat(handle);
    }
    public int     GetPlanarConfiguration (){
        checkHandle();
        return _nGetPlanarConfiguration();
    }
    public boolean IsLossy(){
        checkHandle();
        return _nIsLossy();
    }

    public void SetDimensions(int[] d){
        checkHandle();
        if (d==null)
            throw new NullPointerException("the dimensions array must not be null");
        int[] raw = new int[]{0,0,0};
        for (int i=0;i<d.length;i++){
            raw[i]=d[i];
        }
        _nSetDimensions(raw);
    }
    public void SetLossyFlag(boolean l){
        checkHandle();
        _nSetLossyFlag(l);
    }
    public void SetNeedByteSwap(boolean b){
        checkHandle();
        _nSetNeedByteSwap(b);
    }
    public void SetNumberOfDimensions(int dim){
        checkHandle();
        _nSetNumberOfDimensions(dim);
    }
    public void SetPhotometricInterpretation(PIType pi){
        checkHandle();
        _nSetPhotometricInterpretation(pi.ordinal());
    }

    public  void SetPixelFormat(PixelFormat pf){
        checkHandle();
        if (pf==null)
            throw new NullPointerException("the PixelFormat  must not be null");
        long handle = pf._handle;
        _nSetPixelFormat(handle);
    }

    public void SetPlanarConfiguration(int pc){
        checkHandle();
        _nSetPlanarConfiguration(pc);
    }

    public byte[] decode(byte[] input){
        checkHandle();
        if(input == null)
            throw new NullPointerException("the input bytes  must not be null");
        if(input.length<2)
            return null;
        return _ndecode(input);
    }
    public byte[] code(byte[] input){
        checkHandle();
        if(input == null)
            throw new NullPointerException("the input bytes  must not be null");
        if(input.length<2)
            return null;
        return _ncode(input);
    }
}
