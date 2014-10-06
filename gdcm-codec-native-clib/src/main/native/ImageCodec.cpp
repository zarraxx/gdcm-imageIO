//
//  ImageCodec.cpp
//  gdcmJniAdaptor
//
//  Created by XiaShenPin on 14-10-5.
//  Copyright (c) 2014年 oritsh. All rights reserved.
//
#include "wrap.h"
#include "gdcmImageReader.h"
#include "gdcmImageWriter.h"
#include "gdcmSequenceOfFragments.h"
#include "gdcmJPEGCodec.h"
#include "gdcmJPEG2000Codec.h"
#include "gdcmJPEGLSCodec.h"
#include "gdcmTransferSyntax.h"
#include <sstream>
#include <vector>

#define JPEGDECODEC (0)
#define JPEG2KDECODEC (1)
#define JPEGLSDECODEC (2)

static long GetCHandle(JNIEnv* env,jobject obj){
    jclass clazz =env->GetObjectClass(obj);
    jfieldID field = env->GetFieldID(clazz, "_handle", "J");
    jlong handle = env->GetLongField(obj, field);
    return handle;
}

static gdcm::ImageCodec* GetImageCodec(JNIEnv* env,jobject obj){
    long chandle = GetCHandle(env, obj);
    return (gdcm::ImageCodec*)chandle;
}

JNIEXPORT jlong JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodecFactory_createImageCodec
(JNIEnv *, jclass, jint value){
    gdcm::ImageCodec *codec =NULL ;
    
    switch (value) {
        case JPEGDECODEC:
            codec = new gdcm::JPEGCodec;
            break;
        case JPEG2KDECODEC:
            codec = new gdcm::JPEG2000Codec;
            break;
        case JPEGLSDECODEC:
            codec = new gdcm::JPEGLSCodec;
            break;
        default:
            break;
    }
    return (jlong)codec;
}

JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1ndelete
(JNIEnv * env, jobject obj){
    gdcm::ImageCodec* c = GetImageCodec(env,obj);
    delete c;
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nCanCode
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nCanCode
(JNIEnv * env, jobject obj, jstring jstr){
    gdcm::ImageCodec* c = GetImageCodec(env,obj);
    const char *tsuid;
    tsuid=env->GetStringUTFChars(jstr,JNI_FALSE);
    gdcm::TransferSyntax ts( gdcm::TransferSyntax::GetTSType(tsuid));
    bool b =  c->CanCode(ts);
    env->ReleaseStringUTFChars(jstr,tsuid);
    return (jboolean)b;
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nCanDecode
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nCanDecode
(JNIEnv * env, jobject obj, jstring jstr){
    gdcm::ImageCodec* c = GetImageCodec(env,obj);
    const char *tsuid;
    tsuid=env->GetStringUTFChars(jstr,NULL);
    gdcm::TransferSyntax ts( gdcm::TransferSyntax::GetTSType(tsuid));
    bool b =  c->CanDecode(ts);
    env->ReleaseStringUTFChars(jstr,tsuid);
    return (jboolean)b;
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nGetDimensions
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nGetDimensions
(JNIEnv * env, jobject obj){
    gdcm::ImageCodec* c = GetImageCodec(env,obj);
    int size = 3;//c->GetNumberOfDimensions();
    const unsigned int* d = c->GetDimensions();
    jintArray result = env->NewIntArray(size);
    const jint is[] = {d[0],d[1],d[2]};
    env->SetIntArrayRegion(result, 0, size, is);
    return result;
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nGetHeaderInfo
 * Signature: ([B)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nGetHeaderInfo
(JNIEnv * env, jobject obj, jbyteArray jbytes){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    
    jbyte * byteBuffer = env->GetByteArrayElements(jbytes, JNI_FALSE);
    jsize   bufferSize = env->GetArrayLength(jbytes);
    
    //jsize writeSize = bufferSize >4096?4096:bufferSize;
    
    std::stringstream sstr;
    sstr.write((const char*)byteBuffer, bufferSize);
    sstr.seekg(0);
    env->ReleaseByteArrayElements(jbytes, byteBuffer, JNI_COMMIT);
    
    gdcm::TransferSyntax ts_jpg;
    //gdcm::PixelFormat pf ( gdcm::PixelFormat::UINT8 ); // let's pretend it's a 8bits jpeg
    //pf.SetSamplesPerPixel(3);
    //codec->SetNumberOfDimensions(2);
    //codec->SetPixelFormat( pf );
    bool b = codec->GetHeaderInfo( sstr, ts_jpg );
    
    if (b) {
        jstring jstr = env->NewStringUTF(ts_jpg.GetString());
        return jstr;
    }else{
        return NULL;
    }
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nGetLossyFlag
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nGetLossyFlag
(JNIEnv * env, jobject obj){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    return (jboolean)codec->GetLossyFlag();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nGetNeedByteSwap
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nGetNeedByteSwap
(JNIEnv * env, jobject obj){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    return (jboolean)codec->GetNeedByteSwap();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nGetNumberOfDimensions
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nGetNumberOfDimensions
(JNIEnv * env, jobject obj){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    return (jint)codec->GetNumberOfDimensions();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nGetPhotometricInterpretation
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nGetPhotometricInterpretation
(JNIEnv * env, jobject obj){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    return (jint)codec->GetPhotometricInterpretation().GetType();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nGetPixelFormat
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nGetPixelFormat
(JNIEnv * env, jobject obj){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::PixelFormat pf = codec->GetPixelFormat();
    gdcm::PixelFormat* result = new gdcm::PixelFormat(pf);
    return (jlong)result;
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nGetPlanarConfiguration
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nGetPlanarConfiguration
(JNIEnv * env, jobject obj){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    return (jint)codec->GetPlanarConfiguration();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nIsLossy
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nIsLossy
(JNIEnv * env, jobject obj){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    return (jboolean)codec->IsLossy();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nSetDimensions
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nSetDimensions
(JNIEnv * env, jobject obj, jintArray iarr){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    jint* array = env->GetIntArrayElements(iarr, JNI_FALSE);
    jsize arraySize = env->GetArrayLength(iarr);
    
    std::vector<unsigned int> v;
    for (int i=0; i<arraySize; i++) {
        v.push_back(array[i]);
    }
    codec->SetDimensions(v);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nSetLossyFlag
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nSetLossyFlag
(JNIEnv * env, jobject obj, jboolean b){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    codec->SetLossyFlag(b);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nSetNeedByteSwap
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nSetNeedByteSwap
(JNIEnv * env, jobject obj, jboolean b){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    codec->SetNeedByteSwap(b);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nSetNumberOfDimensions
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nSetNumberOfDimensions
(JNIEnv * env, jobject obj, jint dim){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    codec->SetNumberOfDimensions(dim);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nSetPhotometricInterpretation
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nSetPhotometricInterpretation
(JNIEnv * env, jobject obj, jint pi){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    
    gdcm::PhotometricInterpretation::PIType pitype =
    (gdcm::PhotometricInterpretation::PIType)pi;
    
    codec->SetPhotometricInterpretation(pitype);
    
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nSetPixelFormat
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nSetPixelFormat
(JNIEnv * env, jobject obj, jlong p){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::PixelFormat* pfp = (gdcm::PixelFormat*)p;
    gdcm::PixelFormat pf(*pfp);
    codec->SetPixelFormat(pf);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _nSetPlanarConfiguration
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1nSetPlanarConfiguration
(JNIEnv * env, jobject obj, jint pc){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    codec->SetPlanarConfiguration(pc);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _ndecode
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1ndecode
(JNIEnv * env, jobject obj, jbyteArray inputBytes){
    jbyteArray result=NULL;
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    jbyte * byteBuffer = env->GetByteArrayElements(inputBytes, JNI_FALSE);
    jsize   bufferSize = env->GetArrayLength(inputBytes);
    
    const gdcm::Tag tagPixelData(0x7fe0, 0x0010); // Default to Pixel Data
    gdcm::DataElement compressData(tagPixelData);
    gdcm::DataElement unCompressData(tagPixelData);
    
    gdcm::SmartPointer<gdcm::SequenceOfFragments> sq = new gdcm::SequenceOfFragments;
    gdcm::Fragment frag;
    frag.SetByteValue( (const char*)byteBuffer,bufferSize );
    sq->AddFragment( frag );
    
    compressData.SetValue(*sq);
    
    bool b = codec->Decode(compressData,unCompressData);
    
    if (b) {
        const gdcm::ByteValue* value = unCompressData.GetByteValue();
        if (value) {
            unsigned int byteValueSize = value->GetLength();
            result=env->NewByteArray(byteValueSize);
            env->SetByteArrayRegion(result, 0, byteValueSize, (const signed char*)value->GetPointer());
        }
    }
    
    //jclass cls=env->FindClass("[B");
    
    env->ReleaseByteArrayElements(inputBytes, byteBuffer, JNI_COMMIT);
    return result;
    
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_ImageCodec
 * Method:    _ncode
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_oritsh_imageIO_codec_gdcm_ImageCodec__1ncode
(JNIEnv * env, jobject obj, jbyteArray inputBytes){
    jbyteArray result=NULL;
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    jbyte * byteBuffer = env->GetByteArrayElements(inputBytes, JNI_FALSE);
    jsize   bufferSize = env->GetArrayLength(inputBytes);
    
    const gdcm::Tag tagPixelData(0x7fe0, 0x0010); // Default to Pixel Data
    gdcm::DataElement compressData(tagPixelData);
    gdcm::DataElement unCompressData(tagPixelData);
    
    //    gdcm::SmartPointer<gdcm::SequenceOfFragments> sq = new gdcm::SequenceOfFragments;
    //    gdcm::Fragment frag;
    //    frag.SetByteValue( (const char*)byteBuffer,bufferSize );
    //    sq->AddFragment( frag );
    //
    //    unCompressData.SetValue(*sq);
    unCompressData.SetByteValue((const char*)byteBuffer, bufferSize);
    
    bool b = codec->Code(unCompressData,compressData);
    
    if (b) {
        const gdcm::SequenceOfFragments *sf = compressData.GetSequenceOfFragments();
        if( sf )
        {
            gdcm::Fragment frag = sf->GetFragment(0);
            const gdcm::ByteValue* value = frag.GetByteValue();
            int fragSize = value->GetLength();
            unsigned int byteValueSize = fragSize;
            result=env->NewByteArray(byteValueSize);
            env->SetByteArrayRegion(result, 0, byteValueSize, (const signed char*)value->GetPointer());
            
        }

        
    }
    
    //jclass cls=env->FindClass("[B");
    
    env->ReleaseByteArrayElements(inputBytes, byteBuffer, JNI_COMMIT);
    return result;
}

JNIEXPORT jboolean JNICALL Java_com_oritsh_imageIO_codec_gdcm_JpegCodec__1nGetLossless
(JNIEnv * env, jobject obj){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::JPEGCodec* jpeg = (gdcm::JPEGCodec*)codec;
    return (jboolean)jpeg->GetLossless();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_JpegCodec
 * Method:    _nSetLossless
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_JpegCodec__1nSetLossless
(JNIEnv * env, jobject obj, jboolean b){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::JPEGCodec* jpeg = (gdcm::JPEGCodec*)codec;
    jpeg->SetLossless(b);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_JpegCodec
 * Method:    _nGetQuality
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_com_oritsh_imageIO_codec_gdcm_JpegCodec__1nGetQuality
(JNIEnv * env, jobject obj){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::JPEGCodec* jpeg = (gdcm::JPEGCodec*)codec;
    return (jdouble)jpeg->GetQuality();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_JpegCodec
 * Method:    _nSetQuality
 * Signature: (D)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_JpegCodec__1nSetQuality
(JNIEnv * env, jobject obj, jdouble d){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::JPEGCodec* jpeg = (gdcm::JPEGCodec*)codec;
    jpeg->SetQuality(d);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_JpegLSCodec
 * Method:    _nGetLossless
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_oritsh_imageIO_codec_gdcm_JpegLSCodec__1nGetLossless
(JNIEnv * env, jobject obj){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::JPEGLSCodec* jpeg = (gdcm::JPEGLSCodec*)codec;
    return (jboolean)jpeg->GetLossless();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_JpegLSCodec
 * Method:    _nSetLossless
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_JpegLSCodec__1nSetLossless
(JNIEnv * env, jobject obj, jboolean b){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::JPEGLSCodec* jpeg = (gdcm::JPEGLSCodec*)codec;
    jpeg->SetLossless(b);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec
 * Method:    _nGetQuality
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL Java_com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec__1nGetQuality
(JNIEnv * env, jobject obj, jint idx){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::JPEG2000Codec* jpeg = (gdcm::JPEG2000Codec*)codec;
    return (jdouble)jpeg->GetQuality(idx);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec
 * Method:    _nGetRate
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL Java_com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec__1nGetRate
(JNIEnv * env, jobject obj, jint idx){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::JPEG2000Codec* jpeg = (gdcm::JPEG2000Codec*)codec;
    return (jdouble)jpeg->GetRate(idx);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec
 * Method:    _nSetNumberOfResolutions
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec__1nSetNumberOfResolutions
(JNIEnv * env, jobject obj, jint nres){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::JPEG2000Codec* jpeg = (gdcm::JPEG2000Codec*)codec;
    jpeg->SetNumberOfResolutions(nres);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec
 * Method:    _nSetQuality
 * Signature: (ID)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec__1nSetQuality
(JNIEnv * env, jobject obj, jint idx, jdouble q){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::JPEG2000Codec* jpeg = (gdcm::JPEG2000Codec*)codec;
    jpeg->SetQuality(idx, q);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec
 * Method:    _nSetRate
 * Signature: (ID)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec__1nSetRate
(JNIEnv * env, jobject obj, jint idx, jdouble r){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::JPEG2000Codec* jpeg = (gdcm::JPEG2000Codec*)codec;
    return jpeg->SetRate(idx, r);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec
 * Method:    _nSetReversible
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec__1nSetReversible
(JNIEnv * env, jobject obj, jboolean b){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::JPEG2000Codec* jpeg = (gdcm::JPEG2000Codec*)codec;
    jpeg->SetReversible(b);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec
 * Method:    _nSetTileSize
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_Jpeg2KCodec__1nSetTileSize
(JNIEnv * env, jobject obj, jint tx, jint ty){
    gdcm::ImageCodec* codec = GetImageCodec(env,obj);
    gdcm::JPEG2000Codec* jpeg = (gdcm::JPEG2000Codec*)codec;
    jpeg->SetTileSize(tx, ty);
}


