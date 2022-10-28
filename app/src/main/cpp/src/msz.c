//
// Created by linhui on 2022/10/25.
//

#include <string.h>
#include "msz.h"


void handlerPathToArray(jstring inputPath, jstring outputPath);

char *mallocByte(int len);

JNIEXPORT jint JNICALL
Java_com_example_cpp_SoxUtil_subtraction(JNIEnv *env, jclass class1, jint one, jint tow) {
    int o = one + tow;

    sox_is_playlist("/music.mp3");

    return o;
}


JNIEXPORT jint JNICALL
Java_com_example_cpp_SoxUtil_buildMusic(JNIEnv *env, jclass jclazz, jstring fileInputPath,
                                        jstring fileOutputPath) {
    static sox_format_t *in, *out; /* input and output files */
    sox_effects_chain_t *chain;
    sox_effect_t *e;
    char *args[10];

//    assert(argc == 3);

    /* All libSoX applications must start by initialising the SoX library */
    assert(sox_init() == SOX_SUCCESS);

    char *inputPath = (*env)->GetStringUTFChars(env, fileInputPath, 0);
    char *outputPath = (*env)->GetStringUTFChars(env, fileOutputPath, 0);
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
    args[0] = (char *) in, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
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
    args[0] = (char *) out, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
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


JNIEXPORT int JNICALL
Java_com_example_cpp_SoxUtil_buildMusic2(JNIEnv *env, jclass clazz, jstring inputPath_java,
                                         jstring outputPath_java) {

    char *inputPath = (*env)->GetStringUTFChars(env, inputPath_java, 0);
    char *outputPath = (*env)->GetStringUTFChars(env, outputPath_java, 0);

    static sox_format_t *in, *out; /* input and output files */
    sox_effects_chain_t *chain;
    sox_effect_t *e;
    char *args[10];
    //在使用sox前调用，初始化全局参数
    assert(sox_init() == SOX_SUCCESS);

    //打开输入文件
    assert(in = sox_open_read(inputPath, NULL, NULL, NULL));
    //打开输出文件，必须制定输出信号特征(第二个参数)，这里简单演示，使用in一致的信号特征
    assert(out = sox_open_write(outputPath, &in->signal, NULL, NULL, NULL, NULL));
    //创建一个效果链
    chain = sox_create_effects_chain(&in->encoding, &out->encoding);
    //创建一个最简单的效果，输入文件
    e = sox_create_effect(sox_find_effect("input"));
    args[0] = (char *) in, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    //增加效果到效果链
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    //创建一个最简单的效果bass，输入文件
    e = sox_create_effect(sox_find_effect("bass"));
    args[0] = "20";
    assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    //增加效果到效果链
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

//    //创建一个最简单的效果highpass，输入文件
//    e = sox_create_effect(sox_find_effect("highpass"));
//    args[0] = (char *) in, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
//    //增加效果到效果链
//    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
//    free(e);

    //创建第一段equalizer效果器
    e = sox_create_effect(sox_find_effect("equalizer"));
    args[0] = "100";
    args[1] = "1.5q";
    args[2] = "12";
    assert(sox_effect_options(e, 3, args) == SOX_SUCCESS);
    //增加效果到效果链
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    //创建第二段equalizer效果器
    e = sox_create_effect(sox_find_effect("equalizer"));
    args[0] = "1000";
    args[1] = "2.0q";
    args[2] = "-12";
    assert(sox_effect_options(e, 3, args) == SOX_SUCCESS);
    //增加效果到效果链
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    //输出到文件的效果器
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

    return 0;
}


//JNIEXPORT int JNICALL
//Java_com_example_cpp_SoxUtil_handlerSteam(JNIEnv *env, jclass clazz,jbyteArray) {
//
//
//
//}


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

JNIEXPORT char *mallocByte(int len) {
    return (char *) malloc(len * sizeof(char));
}

jbyteArray JNICALL
Java_com_example_cpp_SoxUtil_handlerSteam(JNIEnv *env, jclass clazz, jbyteArray bytearray) {

    assert(sox_init() == SOX_SUCCESS);


    jbyte *bytes;
    bytes = (*env)->GetByteArrayElements(env, bytearray, 0);
    int chars_len = (*env)->GetArrayLength(env, bytearray);

    char *input_chars = mallocByte(chars_len);;
    char *output_chars = mallocByte(chars_len);

    memset(input_chars, 0, chars_len + 1);
    memcpy(input_chars, bytes, chars_len);
    input_chars[chars_len] = 0;

    (*env)->ReleaseByteArrayElements(env, bytearray, bytes, 0);

    static sox_format_t *out, *in;
//
    assert(in = sox_open_mem_read(input_chars, chars_len, NULL, NULL, NULL));

    assert(out = sox_open_mem_write(output_chars, chars_len, &in->signal, NULL, NULL, NULL));

    sox_effects_chain_t *chain;
    sox_effect_t *e;
    char *args[10];

    chain = sox_create_effects_chain(&in->encoding, &out->encoding);


    e = sox_create_effect(sox_find_effect("bass"));
    args[0] = "50";
    assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    //增加效果到效果链
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    //让整个效果器运行起来，直到遇到eof
    sox_flow_effects(chain, NULL, NULL);

    //clean
    sox_delete_effects_chain(chain);

    sox_close(out);
    sox_close(in);
    sox_quit();

    jbyteArray jbyteArray = (*env)->NewByteArray(env, chars_len);//申明数组，与char字符长度一致
    (*env)->SetByteArrayRegion(env, jbyteArray, 0, chars_len,
                               (jbyte *) output_chars);//赋值到jbyteArray
    return jbyteArray;
}

JNIEXPORT jbyteArray JNICALL
Java_com_example_cpp_SoxUtil_buildMusicByEffectInfo(JNIEnv *env, jclass clazz, jstring effect,
                                                    jcharArray charPare,
                                                    jbyteArray byte_array) {

    if (effect == NULL) {
        printf("effect == null , charPare == null");
        return byte_array;
    }
//    if (*env != NULL) {
//        return byte_array;
//    }

    assert(sox_init() == SOX_SUCCESS);


    jbyte *bytes =  (*env)->GetByteArrayElements(env, byte_array, 0);
    int chars_len = (*env)->GetArrayLength(env, byte_array);

    char *input_chars = mallocByte(chars_len);;
    char *output_chars = mallocByte(chars_len);

    memset(input_chars, 0, chars_len + 1);
    memcpy(input_chars, bytes, chars_len);
    input_chars[chars_len] = 0;

    (*env)->ReleaseByteArrayElements(env, byte_array, bytes, 0);

    static sox_format_t *out, *in;
//
    assert(in = sox_open_mem_read(input_chars, chars_len, NULL, NULL, NULL));

    assert(out = sox_open_mem_write(output_chars, chars_len, &in->signal, NULL, NULL, NULL));

    sox_effects_chain_t *chain;
    sox_effect_t *e;
    char *args[10];

    chain = sox_create_effects_chain(&in->encoding, &out->encoding);


    e = sox_create_effect(sox_find_effect((*env)->GetStringUTFChars(env,effect,0)));
    args[0] = "50";
    assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    //增加效果到效果链
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    //让整个效果器运行起来，直到遇到eof
    sox_flow_effects(chain, NULL, NULL);

    //clean
    sox_delete_effects_chain(chain);

    sox_close(out);
    sox_close(in);
    sox_quit();


    jbyteArray jbyteArray = (*env)->NewByteArray(env, chars_len);//申明数组，与char字符长度一致
    (*env)->SetByteArrayRegion(env, jbyteArray, 0, chars_len,(jbyte *) output_chars);//赋值到jbyteArray
    free(input_chars);
    return jbyteArray;

}