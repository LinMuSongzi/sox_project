//
// Created by linhui on 2022/10/25.
//

#include "msz.h"


JNIEXPORT jint JNICALL
Java_com_example_cpp_SoxUtil_subtraction(JNIEnv *env, jclass class1, jint one, jint tow) {
    int o = one + tow;

    sox_is_playlist("/music.mp3");

    return o;
}


JNIEXPORT jint JNICALL
Java_com_example_cpp_SoxUtil_buildMusic(JNIEnv *env, jclass jclazz, jstring fileInputPath,
                                        jstring fileOutputPath) {
    static sox_format_t * in, * out; /* input and output files */
    sox_effects_chain_t * chain;
    sox_effect_t * e;
    char * args[10];

//    assert(argc == 3);

    /* All libSoX applications must start by initialising the SoX library */
    assert(sox_init() == SOX_SUCCESS);

    char * inputPath = (*env)->GetStringUTFChars(env,fileInputPath, 0);
    char * outputPath = (*env)->GetStringUTFChars(env,fileOutputPath, 0);
    /* Open the input file (with default parameters) */
    assert(in = sox_open_read(inputPath, NULL, NULL, NULL));

    /* Open the output file; we must specify the output signal characteristics.
     * Since we are using only simple effects, they are the same as the input
     * file characteristics */
    assert(out = sox_open_write(outputPath, &in->signal, NULL, NULL, NULL, NULL));

    /* Create an effects chain; some effects need to know about the input
     * or output file encoding so we provide that information here */
    chain = sox_create_effects_chain(&in->encoding, &out->encoding);

    /* The first effect in the effect chain must be something that can source
     * samples; in this case, we use the built-in handler that inputs
     * data from an audio file */
    e = sox_create_effect(sox_find_effect("input"));
    args[0] = (char *)in, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    /* This becomes the first `effect' in the chain */
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    /* Create the `vol' effect, and initialise it with the desired parameters: */
    e = sox_create_effect(sox_find_effect("vol"));
    args[0] = "3dB", assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    /* Add the effect to the end of the effects processing chain: */
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    /* Create the `flanger' effect, and initialise it with default parameters: */
    e = sox_create_effect(sox_find_effect("flanger"));
    assert(sox_effect_options(e, 0, NULL) == SOX_SUCCESS);
    /* Add the effect to the end of the effects processing chain: */
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    /* The last effect in the effect chain must be something that only consumes
     * samples; in this case, we use the built-in handler that outputs
     * data to an audio file */
    e = sox_create_effect(sox_find_effect("output"));
    args[0] = (char *)out, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    /* Flow samples through the effects processing chain until EOF is reached */
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

//    sox_append_comment()

    return 0;//sox_init();
}

JNIEXPORT jint JNICALL
Java_com_example_cpp_SoxUtil_exeuteComment(JNIEnv *env, jclass clazz, jstring order) {

    return 0;
}