//
// Created by linhui on 2022/10/25.
//

#include "msz.h"


int mainJoin(int one, int tow) {


    int o = one + tow;

    sox_is_playlist("/music.mp3");

    return 0;
}

JNIEXPORT jint JNICALL
Java_com_example_cpp_SoxUtil_subtraction(JNIEnv *env, jclass class1, jint one, jint tow) {
    int o = one + tow;

    sox_is_playlist("/music.mp3");

    return o;
}

JNIEXPORT jint JNICALL
Java_com_example_cpp_SoxUtil_buildMusic(JNIEnv *env, jclass jclazz, jstring fileInputPath,
                                        jstring fileOutputPath) {

    static sox_format_t *in, *out;
    sox_effects_chain_t *chain;
    sox_effect_t *e;
    char *args[10];

//    assert(argc == 3);
    assert(sox_init() == SOX_SUCCESS);
    assert((in = sox_open_read(fileInputPath, NULL, NULL, NULL)));

    /* file characteristics */
    assert((out = sox_open_write(fileOutputPath, &in->signal, NULL, NULL, NULL, NULL)));

    /* Create an effects chain */
    chain = sox_create_effects_chain(&in->encoding, &out->encoding);
    e = sox_create_effect(sox_find_effect("input"));
    args[0] = (char *) in, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    /* Create the `vol' effect */
    e = sox_create_effect(sox_find_effect("vol"));
    args[0] = "3dB", assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    /* Create the `flanger' effect */
    e = sox_create_effect(sox_find_effect("flanger"));
    assert(sox_effect_options(e, 0, NULL) == SOX_SUCCESS);
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    /* data to an audio file */
    e = sox_create_effect(sox_find_effect("output"));
    args[0] = (char *) out, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    sox_flow_effects(chain, NULL, NULL);

    /* All done; tidy up: */
    sox_delete_effects_chain(chain);
    sox_close(out);
    sox_close(in);
    sox_quit();
    return 0;
}




/**
 * 在使用SOX库之前，必须初始化整个库的全局参数，需要调用：
 * @param env
 * @param clazz
 * @return
 */
JNIEXPORT int JNICALL
Java_com_example_cpp_SoxUtil_initSox(JNIEnv *env, jclass clazz) {
    return 0;//sox_init();
}

JNIEXPORT jint JNICALL
Java_com_example_cpp_SoxUtil_exeuteComment(JNIEnv *env, jclass clazz, jstring order) {

    return 0;
}