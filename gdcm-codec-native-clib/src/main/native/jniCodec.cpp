#include "wrap.h"
#include "gdcmImageReader.h"
#include "gdcmImageWriter.h"
#include "gdcmSequenceOfFragments.h"
#include "gdcmJPEGCodec.h"
#include "gdcmJPEG2000Codec.h"
#include "gdcmJPEGLSCodec.h"
#include "gdcmTransferSyntax.h"
#include <sstream>

#define JPEGDECODEC (0)
#define JPEG2KDECODEC (1)
#define JPEGLSDECODEC (2)

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Decodec
 * Method:    _ninitCodec
 * Signature: (Lcom/oritsh/imageIO/codec/gdcm/Decodec/GdcmDecodec;)J
 */
JNIEXPORT jlong JNICALL Java_com_oritsh_imageIO_codec_gdcm_Decodec__1ninitCodec
(JNIEnv *, jobject, jint value){
    
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

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Decodec
 * Method:    _nclose
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_Decodec__1nclose
(JNIEnv *, jobject, jlong p){
    gdcm::ImageCodec *codec = (gdcm::ImageCodec *)p;
    delete codec;
    return ;
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Decodec
 * Method:    _ngetHeaderInfo
 * Signature: (J[B)Z
 */
JNIEXPORT jboolean JNICALL Java_com_oritsh_imageIO_codec_gdcm_Decodec__1ngetHeaderInfo
(JNIEnv * env, jobject, jlong p, jbyteArray jbytes){
    gdcm::ImageCodec *codec = (gdcm::ImageCodec *)p;
    jbyte * byteBuffer = env->GetByteArrayElements(jbytes, JNI_FALSE);
    jsize   bufferSize = env->GetArrayLength(jbytes);
    
    jsize writeSize = bufferSize >4096?4096:bufferSize;
    
    std::stringstream sstr;
    sstr.write((const char*)byteBuffer, writeSize);
    sstr.seekg(0);
    
    gdcm::TransferSyntax ts_jpg;
    gdcm::PixelFormat pf ( gdcm::PixelFormat::UINT8 ); // let's pretend it's a 8bits jpeg
    pf.SetSamplesPerPixel(3);
    codec->SetNumberOfDimensions(2);
    codec->SetPixelFormat( pf );
    bool b = codec->GetHeaderInfo( sstr, ts_jpg );
    env->ReleaseByteArrayElements(jbytes, byteBuffer, JNI_COMMIT);
    return b;
    
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Decodec
 * Method:    _ngetWidth
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_Decodec__1ngetWidth
(JNIEnv *, jobject, jlong p){
    gdcm::ImageCodec *codec = (gdcm::ImageCodec *)p;
    int width = codec->GetDimensions()[0];
    //int height = jpeg->GetDimensions()[1];
    return (jint)width;
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Decodec
 * Method:    _ngetHeight
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_Decodec__1ngetHeight
(JNIEnv *, jobject, jlong p){
    gdcm::ImageCodec *codec = (gdcm::ImageCodec *)p;
    //int width = codec->GetDimensions()[0];
    int height = codec->GetDimensions()[1];
    return (jint)height;
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Decodec
 * Method:    _ngetBitsAllocated
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_Decodec__1ngetBitsAllocated
(JNIEnv *, jobject, jlong p){
    gdcm::ImageCodec *codec = (gdcm::ImageCodec *)p;
    gdcm::PixelFormat pf = codec->GetPixelFormat();
    return (jint)pf.GetBitsAllocated();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Decodec
 * Method:    _ngetSamplesPerPixel
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_Decodec__1ngetSamplesPerPixel
(JNIEnv *, jobject, jlong p){
    gdcm::ImageCodec *codec = (gdcm::ImageCodec *)p;
    gdcm::PixelFormat pf = codec->GetPixelFormat();
    return (jint)pf.GetSamplesPerPixel();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_Decodec
 * Method:    decode
 * Signature: (J[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_oritsh_imageIO_codec_gdcm_Decodec__1ndecode
(JNIEnv * env, jobject, jlong p, jbyteArray inputBytes){
    jbyteArray result=NULL;
    gdcm::ImageCodec *codec = (gdcm::ImageCodec *)p;
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
        unsigned int byteValueSize = unCompressData.GetByteValue()->GetLength();
        result=env->NewByteArray(byteValueSize);
        env->SetByteArrayRegion(result, 0, byteValueSize, (const signed char*)unCompressData.GetByteValue()->GetPointer());
        
    }
    
    
    //jclass cls=env->FindClass("[B");
    
    env->ReleaseByteArrayElements(inputBytes, byteBuffer, JNI_COMMIT);
    return result;
    
}


