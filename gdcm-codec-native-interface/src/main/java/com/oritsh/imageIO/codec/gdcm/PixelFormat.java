package com.oritsh.imageIO.codec.gdcm;

/**
 * Created by zarra on 14-10-5.
 */
public class PixelFormat {

    public enum ScalarType{
                UINT8,
                INT8,
                UINT12,
                INT12,
                UINT16,
                INT16,
                UINT32,  // For some DICOM files (RT or SC)
                INT32,   //                        "   "
                FLOAT16, // sure why not...
                FLOAT32, // good ol' 'float'
                FLOAT64, // aka 'double'
                SINGLEBIT, // bool / monochrome
                UNKNOWN // aka BitsAllocated == 0 && PixelRepresentation == 0
    } ;

    protected long _handle;

    native private long _nnew();
    native private void _ndelete();

    native private int _ngetBitsAllocated();
    native private int _ngetBitsStored();
    native private int _ngetHightBit();
    native private int _ngetMax();
    native private int _ngetMin();
    native private int _ngetPixelRepresentation();
    native private int _ngetPixelSize();
    native private int _ngetSamplesPerPixel();
    native private int _ngetScalarType();
    native private String _ngetScalarTypeAsString();
    native boolean _nisValid();

    native private void _nsetBitsAllocated(int ba);
    native private void _nsetBitsStored(int bs);
    native private void _nsetHightBit(int hb);
    native private void _nsetPixelRepresentation(int pr);
    native private void _nsetSamplesPerPixel(int spp);
    native private void _nsetScalarType(int st);

    public PixelFormat(){
        _handle = -1;
        _handle = _nnew();
    }

    public PixelFormat(long handle){
        _handle = handle;
    }

    public void dispose(){
        if (_handle!=-1){
            _ndelete();
            _handle = -1;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    private void checkHandle(){
        if (_handle == -1){
            throw  new NativeHandleException("native handle is destroy!");
        }
    }

    public int getBitsAllocated(){
        checkHandle();
        return _ngetBitsAllocated();
    }

    public void setBitsAllocated(int ba){
        checkHandle();
        _nsetBitsAllocated(ba);
    }

    public int getBitsStored(){
        checkHandle();
        return _ngetBitsStored();
    }

    public void setBitsStored(int bs){
        checkHandle();
        _nsetBitsStored(bs);
    }

    public int getHightBit(){
        checkHandle();
        return _ngetHightBit();
    }

    public void setHightBit(int hb){
        checkHandle();
        _nsetHightBit(hb);
    }

    public int getPixelRepresentation(){
        checkHandle();
        return _ngetPixelRepresentation();
    }

    public void setPixelRepresentation(int ps){
        checkHandle();
        _nsetPixelRepresentation(ps);
    }

    public int getSamplesPerPixel(){
        checkHandle();
        return _ngetSamplesPerPixel();
    }

    public void setSamplesPerPixel(int pps){
        checkHandle();
        _nsetSamplesPerPixel(pps);
    }

    public ScalarType getScalarType(){
        checkHandle();
        int index = _ngetScalarType();
        return ScalarType.values()[index];
    }

    public void setScalarType(ScalarType scalarType){
        checkHandle();
        _nsetScalarType(scalarType.ordinal());
    }

    public boolean isValid(){
        checkHandle();
        return _nisValid();
    }

    public String getScalarTypeAsString(){
        checkHandle();
        return _ngetScalarTypeAsString();
    }
}
