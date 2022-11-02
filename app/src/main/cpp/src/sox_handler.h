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

JNIEXPORT jbyteArray JNICALL
Java_com_example_cpp_SoxUtil_buildMusicByEffectInfo(JNIEnv *env, jclass clazz, jstring effect,jstring value,jbyteArray byte_array);