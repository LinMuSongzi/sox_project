//
// Created by linhui on 2022/10/25.
//
//#include <mszi_test.h>
#include "mszi_test.h"
#include "text_sum/subtraction.h"
extern "C" jint Java_com_example_cpp_SumApi_sum(JNIEnv *env, jclass clazz, jint one, jint tow) {


    return one + tow;

}

extern "C" jint
Java_com_example_cpp_SumApi_subtraction(JNIEnv *env, jclass clazz, jint one, jint tow) {


    return subtraction(one,tow);

}