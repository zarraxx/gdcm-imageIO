//
//  PixelFormat.cpp
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

static long GetCHandle(JNIEnv* env,jobject obj){
    jclass clazz =env->GetObjectClass(obj);
    jfieldID field = env->GetFieldID(clazz, "_handle", "J");
    jlong handle = env->GetLongField(obj, field);
    return handle;
}

static gdcm::PixelFormat* GetCObj(JNIEnv* env,jobject obj){
    long chandle = GetCHandle(env, obj);
    return (gdcm::PixelFormat*)chandle;
}


/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _nnew
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1nnew
(JNIEnv *, jobject){
    gdcm::PixelFormat* handle = new gdcm::PixelFormat;
    return (jlong)handle;
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _ndelete
 * Signature: ()J
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1ndelete
(JNIEnv * env , jobject obj){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    delete handle;
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _ngetBitsAllocated
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1ngetBitsAllocated
(JNIEnv * env, jobject obj){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    return (jint)handle->GetBitsAllocated();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _ngetBitsStored
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1ngetBitsStored
(JNIEnv * env, jobject obj){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    return (jint)handle->GetBitsStored();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _ngetHightBit
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1ngetHightBit
(JNIEnv * env, jobject obj){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    return (jint)handle->GetHighBit();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _ngetMax
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1ngetMax
(JNIEnv * env, jobject obj){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    return (jint)handle->GetMax();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _ngetMin
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1ngetMin
(JNIEnv * env, jobject obj){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    return (jint)handle->GetMin();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _ngetPixelRepresentation
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1ngetPixelRepresentation
(JNIEnv * env, jobject obj){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    return (jint)handle->GetPixelRepresentation();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _ngetPixelSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1ngetPixelSize
(JNIEnv * env, jobject obj){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    return (jint)handle->GetPixelSize();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _ngetSamplesPerPixel
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1ngetSamplesPerPixel
(JNIEnv * env, jobject obj){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    return (jint)handle->GetSamplesPerPixel();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _ngetScalarType
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1ngetScalarType
(JNIEnv * env, jobject obj){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    return (jint)handle->GetScalarType();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _ngetScalarTypeAsString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1ngetScalarTypeAsString
(JNIEnv * env , jobject obj){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    const char* str = handle->GetScalarTypeAsString();
    jstring jstr=env->NewStringUTF(str);
    return jstr;
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _nisValid
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1nisValid
(JNIEnv * env, jobject obj){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    return (jboolean)handle->IsValid();
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _nsetBitsAllocated
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1nsetBitsAllocated
(JNIEnv * env, jobject obj, jint value){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    handle->SetBitsAllocated(value);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _nsetBitsStored
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1nsetBitsStored
(JNIEnv * env, jobject obj, jint value){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    handle->SetBitsStored(value);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _nsetHightBit
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1nsetHightBit
(JNIEnv * env, jobject obj, jint value){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    handle->SetHighBit(value);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _nsetPixelRepresentation
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1nsetPixelRepresentation
(JNIEnv * env, jobject obj, jint value){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    handle->SetPixelRepresentation(value);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _nsetSamplesPerPixel
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1nsetSamplesPerPixel
(JNIEnv * env, jobject obj, jint value){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    handle->SetSamplesPerPixel(value);
}

/*
 * Class:     com_oritsh_imageIO_codec_gdcm_PixelFormat
 * Method:    _nsetScalarType
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_oritsh_imageIO_codec_gdcm_PixelFormat__1nsetScalarType
(JNIEnv * env, jobject obj, jint value){
    gdcm::PixelFormat* handle = GetCObj(env, obj);
    handle->SetScalarType((gdcm::PixelFormat::ScalarType)value);
}