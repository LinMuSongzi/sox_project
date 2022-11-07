//
// Created by linhui on 2022/11/2.
//

#ifndef CPP_SOX_HANDLER_H
#define CPP_SOX_HANDLER_H

#include "sox.h"
#include <jni.h>
#include <malloc.h>
#include <assert.h>
#include <android/log.h>
#include <string.h>

#endif //CPP_SOX_HANDLER_H

char * mallocByte2(int len) {
    return (char *) malloc(len * sizeof(char));
}

JNIEXPORT jint JNICALL
Java_com_psyone_sox_SoxProgramHandler_destoryWorkContext(JNIEnv *env, jobject thiz, jint context);

JNIEXPORT jbyteArray JNICALL
Java_com_psyone_sox_SoxProgramHandler_dequeueOutput(JNIEnv *env, jobject thiz, jint context);

JNIEXPORT jint JNICALL
Java_com_psyone_sox_SoxProgramHandler_queueInput(JNIEnv *env, jobject thiz, jint context,
                                                 jbyteArray byte_array, jint start_index,
                                                 jint leng);
JNIEXPORT jint JNICALL
Java_com_psyone_sox_SoxProgramHandler_onAudioConfig(JNIEnv *env, jobject thiz, jint context,
                                                    jint sample_rate, jint channels, jint bit);
JNIEXPORT jint JNICALL
Java_com_psyone_sox_SoxProgramHandler_createAudioWorkConetxt(JNIEnv *env, jobject thiz,
                                                             jobject context, jint other);
JNIEXPORT jbyteArray JNICALL
Java_com_example_cpp_SoxUtil_buildMusicByEffectInfo(JNIEnv *env, jclass clazz, jstring effect,jstring value,jbyteArray byte_array);




