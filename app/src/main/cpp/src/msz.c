//
// Created by linhui on 2022/10/25.
//

#include <string.h>
#include "msz.h"
//#include


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


JNIEXPORT char *mallocByte(int len) {
    return (char *) malloc(len * sizeof(char));
}

const char * TAG = "SOX";

JNIEXPORT jbyteArray JNICALL
Java_com_example_cpp_SoxUtil_buildMusicByEffectInfo(JNIEnv *env, jclass clazz, jstring effect,
                                                    jstring value,
                                                    jbyteArray byte_array) {
    int init_size2 = (*env)->GetArrayLength(env, byte_array);

    assert(sox_init() == SOX_SUCCESS);
//    jsize size = (*env)->GetArrayLength(env,byte_array);
    int init_size = (*env)->GetArrayLength(env, byte_array);

//    size_t si

    size_t size = init_size;

    char *bytearr = mallocByte(init_size);
    char *output_chars ;//= mallocByte(init_size);
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
    args[0] = (char *)in, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    /* This becomes the first `effect' in the chain */
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    //2
    e = sox_create_effect(sox_find_effect("bass"));
    args[0] = (*env)->GetStringUTFChars(env,value,0);//"10";
    assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    //增加效果到效果链
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    e = sox_create_effect(sox_find_effect("vol"));
    args[0] = "5dB", assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    /* Add the effect to the end of the effects processing chain: */
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    e = sox_create_effect(sox_find_effect("output"));
    args[0] = (char *)out, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
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
//    __android_log_print(ANDROID_LOG_INFO, "SOX", "%s", "输出~~~~~~");


    free(bytearr);
    free(output_chars);
//    free((void *) size);
    return jbyteArray;

}


/**
 * 这个的确可以完成业务需求但是这样
 * 不太好
 * @param env
 * @param clazz
 * @param effect_real_name
 * @param values
 * @param outputPath_java
 * @param char_array
 * @param byte_array
 * @return
 */
JNIEXPORT jint JNICALL
Java_com_example_cpp_SoxUtil_buildMusicByEffectInfoFile(JNIEnv *env, jclass clazz,
                                                        jstring effect_real_name,
                                                        jstring values, jstring outputPath_java,
                                                        jcharArray char_array,
                                                        jbyteArray byte_array) {
    int init_size2 = (*env)->GetArrayLength(env, byte_array);
//    if (effect_real_name == NULL) {
//        return 0;
//    }
    char *outputPath = (*env)->GetStringUTFChars(env, outputPath_java, 0);
    static sox_format_t * in, * out; /* input and output files */
    sox_effects_chain_t * chain;
    sox_effect_t * e;
    char * args[10];

    char *bytearr = mallocByte(init_size2);
//    char *output_chars = mallocByte(init_size2);
    (*env)->GetByteArrayRegion(env, byte_array, 0, init_size2, bytearr);

//    assert(argc == 3);

    /* All libSoX applications must start by initialising the SoX library */
    assert(sox_init() == SOX_SUCCESS);

    /* Open the input file (with default parameters) */
    assert(in = sox_open_mem_read(bytearr, init_size2, NULL, NULL,"wav"));

    /* Open the output file; we must specify the output signal characteristics.
     * Since we are using only simple effects, they are the same as the input
     * file characteristics */
    assert(out = sox_open_write(outputPath, &in->signal, NULL, "wav", NULL, NULL));

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

    //(*env)->GetStringUTFChars(env, effect_real_name, 0)
    //创建一个最简单的效果bass，输入文件
    e = sox_create_effect(sox_find_effect("bass"));
    if(values!=NULL) {
        args[0] = (*env)->GetStringUTFChars(env, values, 0);
    }else{
        args[0] = "10";
    }
    assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    //增加效果到效果链
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