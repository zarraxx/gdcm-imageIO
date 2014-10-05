package com.oritsh.imageIO.codec.gdcm;

/**
 * Created by zarra on 14-10-5.
 */
public class Jpeg2KCodec extends ImageCodec {
    @Override
    protected long newCodec() {
        return ImageCodecFactory.createImageCodec(ImageCodecFactory.Codec.JPEG2KDECODEC);
    }

    native private double 	_nGetQuality ( int idx) ;
    native private double 	_nGetRate ( int idx) ;
    native private void 	_nSetNumberOfResolutions ( int nres);
    native private void 	_nSetQuality ( int idx, double q);
    native private void 	_nSetRate ( int idx, double rate);
    native private void 	_nSetReversible (boolean res);
    native private void 	_nSetTileSize ( int tx,  int ty);

    public double 	GetQuality ( int idx) {
        return _nGetQuality(idx);
    }
    public double 	GetRate ( int idx) {
        return _nGetRate(idx);
    }
    public void 	SetNumberOfResolutions ( int nres){
        _nSetNumberOfResolutions(nres);
    }
    public void 	SetQuality ( int idx, double q){
        _nSetQuality(idx,q);
    }
    public void 	SetRate ( int idx, double rate){
        _nSetRate(idx,rate);
    }
    public void 	SetReversible (boolean res){
        _nSetReversible(res);
    }
    public void 	SetTileSize ( int tx,  int ty){
        _nSetTileSize(tx,ty);
    }
}
