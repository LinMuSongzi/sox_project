//
// Created by linhui on 2022/10/25.
//

#ifndef CPP_MSZI_TEST_H
#define CPP_MSZI_TEST_H

#include <jni.h>
#include <string>


extern "C" JNIEXPORT jint
Java_com_example_cpp_SumApi_sum(_JNIEnv *env,jclass clazz, int one, int tow);
extern "C" JNIEXPORT jint
Java_com_example_cpp_SumApi_subtraction(_JNIEnv *env,jclass clazz, int one, int tow);

#endif //CPP_MSZI_TEST_H
