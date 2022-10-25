//
// Created by linhui on 2022/10/25.
//

#include "msz.h"
#include <jni.h>
#include <malloc.h>


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

//    jint result = 0;

//    if (filePath != NULL) {
    sox_init();
    /**
     * 接下来初始化输入文件 input
     */
    sox_format_t *in;
    const char *in_path = fileInputPath;
    in = sox_open_read(in_path, NULL, NULL, NULL);

    /**
    * 再来初始化输出文件 output
    */
    sox_format_t *out;
    const char *out_path = fileOutputPath;//"/sdcard/千山万水out.mp3";
    out = sox_open_write(out_path, &in->signal, NULL, NULL, NULL, NULL);

    /**
     * sox使用类似责任链设计模式的方式设计整个系统，所以使用时需要先构造一下效果器链，然后将使用的效果器一个一个加到这个效果链中。先来构造这个效果器链，代码如下：
     */
    sox_effects_chain_t *chain;
    chain = sox_create_effects_chain(&in->encoding, &out->encoding);

    /**
     * 上述代码构造出了一个效果器链，重要的是里面的两个参数，这两个参数实际上就是确定输入，
     * 输出的数据格式，比如声道数，采样率，位宽等。这些数据已经从初始化sox_format_t的时候拿到了，
     * 会存进相应encoding属性中，有了效果器链，就可以构建效果器，然后添加进效果器链。
     * 先来构建一下输入数据的效果器：（所有支持的效果器可以去effects.h这个头文件中查看）
     */
    sox_effect_t *inputEffect;
    inputEffect = sox_create_effect(sox_find_effect("input"));

    /**
     * 上述代码构造了一个输入数据的效果器，但是数据从哪来呢，怎么将上面的输入文件配置到这个效果器中呢，代码如下
     */
    char *args[10];
    args[0] = (char *) in;
    sox_effect_options(inputEffect, 1, args);//参数含义：效果器，参数个数，具体参数

    /**
     * 目前已经给inputEffect效果器配置好了需要的参数，然后要将这个效果器增加到效果器链中，并将这个效果器释放掉，代码如下
     */
    sox_add_effect(chain, inputEffect, &in->signal, &in->signal);
    free(inputEffect);

    /**
     * 到此已经把这个输入效果器添加到了效果器链中，接下来在加入一个增加音量的效果器，添加代码如下
     */
    sox_effect_t *volumeEffect;
    volumeEffect = sox_create_effect(sox_find_effect("vol"));
    args[0] = "20db";
    sox_effect_options(volumeEffect, 1, args);
    sox_add_effect(chain, volumeEffect, &in->signal, &in->signal);
    free(volumeEffect);

    /**
     * 到此一个增加20db增加音量效果器添加完成。
     * 接下来添加一个输出数据的效果器，代码如下
     */
    sox_effect_t *outputEffect;
    outputEffect = sox_create_effect(sox_find_effect("output"));
    args[0] = (char *) out;
    sox_effect_options(outputEffect, 1, args);
    sox_add_effect(chain, outputEffect, &in->signal, &in->signal);
    free(outputEffect);

    /**
     * 到此这个效果器链构建完好了:
     * input.wav—>inputEffect------>volumeEffect------->outputEffect------>output.wav
     * 要让效果器链运行起来，要执行如下代码
     */
    sox_flow_effects(chain, NULL, NULL);

    /**
     * 这个方法执行结束，整个处理流程就结束了，增加音量后的音频数据已经全部被写入output.wav。
     * 处理完后就要销毁这个效果器链了
     */
    sox_delete_effects_chain(chain);
    sox_close(out);
    sox_close(in);

    /**
     * 上述函数返回一个整数，如果返回的是SOX_SUCCESS这个枚举值，代码初始化成功；
     * 退出SOX则需要调用如下代码
     */
    sox_quit();

    return 1;
}




/**
 * 在使用SOX库之前，必须初始化整个库的全局参数，需要调用：
 * @param env
 * @param clazz
 * @return
 */
JNIEXPORT int JNICALL
Java_com_example_cpp_SoxUtil_initSox(JNIEnv *env, jclass clazz) {
    return sox_init();
}

JNIEXPORT jint JNICALL
Java_com_example_cpp_SoxUtil_exeuteComment(JNIEnv *env, jclass clazz, jstring order) {

    return 0;
}