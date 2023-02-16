//
// Created by linhui on 2022/11/2.
//

#include "sox_handler.h"

JNIEXPORT jbyteArray JNICALL
Java_com_psyone_sox_SoxProgramHandler_exampleConvertByPcmData(JNIEnv *env, jclass clazz,
                                                              jbyteArray byte_array,
                                                              jstring value) {

    int init_size2 = (*env)->GetArrayLength(env, byte_array);

    assert(sox_init() == SOX_SUCCESS);
//    jsize size = (*env)->GetArrayLength(env,byte_array);
    int init_size = (*env)->GetArrayLength(env, byte_array);

//    size_t si

    size_t size = init_size;

    char *bytearr = mallocByte2(init_size);
    char *output_chars;//= mallocByte(init_size);
    (*env)->GetByteArrayRegion(env, byte_array, 0, init_size, bytearr);

    static sox_format_t *out, *in;

    assert(in = sox_open_mem_read(bytearr, init_size, NULL, NULL, "wav"));

    assert(out = sox_open_memstream_write(&output_chars, &size, &in->signal, NULL, "wav", NULL));

    sox_effects_chain_t *chain;
    sox_effect_t *e;
    char *args[10];

    chain = sox_create_effects_chain(&in->encoding, &out->encoding);

    //1
    e = sox_create_effect(sox_find_effect("input"));
    args[0] = (char *) in, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    /* This becomes the first `effect' in the chain */
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    if(value!=NULL) {

        e = sox_create_effect(sox_find_effect("bass"));
        args[0] = (*env)->GetStringUTFChars(env, value, 0);
        assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
        //增加效果到效果链
        assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
        free(e);
    }

//    e = sox_create_effect(sox_find_effect("trim"));
//    args[0] = "10", assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
//    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
//    free(e);




    e = sox_create_effect(sox_find_effect("output"));
    args[0] = (char *) out, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    //让整个效果器运行起来，直到遇到eof
    sox_flow_effects(chain, NULL, NULL);

    //clean
    sox_delete_effects_chain(chain);

    sox_close(out);
    sox_close(in);
    sox_quit();

//    __android_log_print(ANDROID_LOG_INFO, "SOX", "output大小 =  %d",sizeof());

    jbyteArray jbyteArray = (*env)->NewByteArray(env, init_size2);//申明数组，与char字符长度一致
    const jbyte *b = (const jbyte *) output_chars;
    (*env)->SetByteArrayRegion(env, jbyteArray, 0, init_size2, b);//赋值到jbyteArray

    free(bytearr);
    free(output_chars);
    return jbyteArray;


}

JNIEXPORT jint JNICALL
Java_com_psyone_sox_SoxProgramHandler_destoryWorkContext(JNIEnv *env, jobject thiz, jint context) {
    return 1;
};

JNIEXPORT jbyteArray JNICALL
Java_com_psyone_sox_SoxProgramHandler_dequeueOutput(JNIEnv *env, jobject thiz,
                                                    jint context) { return 1; };

JNIEXPORT jint JNICALL
Java_com_psyone_sox_SoxProgramHandler_queueInput(JNIEnv *env, jobject thiz, jint context,
                                                 jbyteArray byte_array, jint start_index,
                                                 jint leng) { return 1; }

JNIEXPORT jint JNICALL
Java_com_psyone_sox_SoxProgramHandler_onAudioConfig(JNIEnv *env, jobject thiz, jint context,
                                                    jint sample_rate, jint channels,
                                                    jint bit) { return 1; }

JNIEXPORT jint JNICALL
Java_com_psyone_sox_SoxProgramHandler_createAudioWorkConetxt(JNIEnv *env, jobject thiz,
                                                             jobject context,
                                                             jint other) { return 1; }


JNIEXPORT jbyteArray JNICALL
Java_com_psyone_sox_SoxProgramHandler_exampleConvertByPcmData2(JNIEnv *env, jclass clazz,
                                                               jbyteArray byte_array,
                                                               jobject music_effecy_bean,
                                                               jint simple_rate, jint channel,
                                                               jint bit) {
    int init_size2 = (*env)->GetArrayLength(env, byte_array);

    assert(sox_init() == SOX_SUCCESS);
//    jsize size = (*env)->GetArrayLength(env,byte_array);
    int init_size = (*env)->GetArrayLength(env, byte_array);

//    size_t si

    size_t size = init_size;

    char *bytearr = mallocByte2(init_size);
    char *output_chars;//= mallocByte(init_size);
    (*env)->GetByteArrayRegion(env, byte_array, 0, init_size, bytearr);

    static sox_format_t *out, *in;

    assert(in = sox_open_mem_read(bytearr, init_size, NULL, NULL, "wav"));

    assert(out = sox_open_memstream_write(&output_chars, &size, &in->signal, NULL, "wav", NULL));

    sox_effects_chain_t *chain;
    sox_effect_t *e;
    char *args[10];

    chain = sox_create_effects_chain(&in->encoding, &out->encoding);

    //1
    e = sox_create_effect(sox_find_effect("input"));
    args[0] = (char *) in, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    /* This becomes the first `effect' in the chain */
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);


    if( music_effecy_bean != NULL) {
        e = sox_create_effect(sox_find_effect("bass"));
        args[0] = (*env)->GetStringUTFChars(env, "10", 0);
        assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
        //增加效果到效果链
        assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
        free(e);
    }


//    e = sox_create_effect(sox_find_effect("trim"));
//    args[0] = "10", assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
//    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
//    free(e);




    e = sox_create_effect(sox_find_effect("output"));
    args[0] = (char *) out, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    //让整个效果器运行起来，直到遇到eof
    sox_flow_effects(chain, NULL, NULL);

    //clean
    sox_delete_effects_chain(chain);

    sox_close(out);
    sox_close(in);
    sox_quit();

//    __android_log_print(ANDROID_LOG_INFO, "SOX", "output大小 =  %d",sizeof());

    jbyteArray jbyteArray = (*env)->NewByteArray(env, init_size2);//申明数组，与char字符长度一致
    const jbyte *b = (const jbyte *) output_chars;
    (*env)->SetByteArrayRegion(env, jbyteArray, 0, init_size2, b);//赋值到jbyteArray

    free(bytearr);
    free(output_chars);
    return jbyteArray;

}