//
// Created by linhui on 2022/11/2.
//
#include "sox.h"
#include <jni.h>
#include <malloc.h>
#include <assert.h>
#include <android/log.h>
#include <string.h>

JNIEXPORT jbyteArray JNICALL Java_com_psyone_sox_SoxProgramHandler_exampleConvertByPcmData(JNIEnv *env, jclass clazz,
                                                                                           jbyteArray byte_array,  jint type, jstring v1, jstring v2, jstring v3, jstring v4,
                                                                                           jint simple_rate, jint channel,  jint bit) {
    int len = (*env)->GetArrayLength(env, byte_array);
    int len1 = len;
    char* input_pcmdata = (char*) (*env)->GetByteArrayElements(env, byte_array, NULL);
    char* output_pcmdata;

    static sox_format_t *out, *in;
    assert(sox_init() == SOX_SUCCESS);
    assert(in = sox_open_mem_read(input_pcmdata, len, NULL, NULL, "wav"));
    assert(out = sox_open_memstream_write(&output_pcmdata, &len, &in->signal, NULL, "wav", NULL));

    sox_effects_chain_t *chain;
    sox_effect_t *e;
    char *args[10];
    chain = sox_create_effects_chain(&in->encoding, &out->encoding);
    e = sox_create_effect(sox_find_effect("input"));
    args[0] = (char *) in, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    char* p1 = (*env)->GetStringUTFChars(env, v1, NULL);
    char* p2 = (*env)->GetStringUTFChars(env, v2, NULL);
    char* p3 = (*env)->GetStringUTFChars(env, v3, NULL);
    char* p4 = (*env)->GetStringUTFChars(env, v4, NULL);
    __android_log_print(ANDROID_LOG_INFO, "SOX", "type = %d para=%s %s %s %s",type,p1,p2,p3,p4);
    /* 以下为效果器 */
    switch (type) {
        case 1: { //低音增强
            e = sox_create_effect(sox_find_effect("bass"));
            args[0] = p1, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
            assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
            free(e);
            __android_log_print(ANDROID_LOG_INFO, "SOX", "bass+%s",p1);
            break;
        }
        case 2: { //音调 -1000=大叔 这个适合针对语音，带音乐不适合,参数不能为负
//            e = sox_create_effect(sox_find_effect("flanger"));
            e = sox_create_effect(sox_find_effect("pitch"));
            args[0] = p1, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
            assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
            free(e);
            __android_log_print(ANDROID_LOG_INFO, "SOX", "pitch+%s",p1);
            break;
        }
        case 3: { //remix 左右声道输出为 1 /2 /0/1,2 如果1,0会报错 1v0.2,2v1.0
            e = sox_create_effect(sox_find_effect("remix"));
            args[0] = p1, args[1] = p2, assert(sox_effect_options(e, 2, args) == SOX_SUCCESS);
            assert(sox_add_effect(chain, e, &in->signal, &out->signal) == SOX_SUCCESS);
            free(e);
            __android_log_print(ANDROID_LOG_INFO, "SOX", "remix 2 1");
            break;
        }
        case 4: { //均衡器 有变化，说不出
            e = sox_create_effect(sox_find_effect("equalizer"));
            args[0] = p1, args[1] = p2, args[2] = p3;
            assert(sox_effect_options(e, 3, args) == SOX_SUCCESS);
            assert(sox_add_effect(chain, e, &in->signal, &out->signal) == SOX_SUCCESS);
            free(e);
            __android_log_print(ANDROID_LOG_INFO, "SOX", "equalizer %s %s %s",p1,p2,p3);
            break;
        }
        case 5: { //混响 听不出来效果
            e = sox_create_effect(sox_find_effect("reverb"));
            assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
            free(e);
            __android_log_print(ANDROID_LOG_INFO, "SOX", "reverb");
            break;
        }
        case 6: { //搞怪
            e = sox_create_effect(sox_find_effect("tempo"));
            args[0] = p1, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
            assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
            free(e);
            __android_log_print(ANDROID_LOG_INFO, "SOX", "tempo %s",p1);
            break;
        }
        case 7: { //空灵 听不出
            e = sox_create_effect(sox_find_effect("echo"));
            args[0] = p1, args[1] = p2, args[2] = p3, args[3] = p4;
            assert(sox_effect_options(e, 4, args) == SOX_SUCCESS);
            assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
            free(e);
            __android_log_print(ANDROID_LOG_INFO, "SOX", "echo %s %s %s %s",p1,p2,p3,p4);
            break;
        }
        case 8: { //颤抖，效果有
            e = sox_create_effect(sox_find_effect("tremolo"));
            args[0] = p1, args[1] = p2, assert(sox_effect_options(e, 2, args) == SOX_SUCCESS);
            assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
            free(e);
            __android_log_print(ANDROID_LOG_INFO, "SOX", "tremolo %s %s",p1,p2);
            break;
        }
        case 9: { //增益 =强行拉高音量
            e = sox_create_effect(sox_find_effect("gain"));
            args[0] = p1, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
            assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
            free(e);
            __android_log_print(ANDROID_LOG_INFO, "SOX", "gain %s",p1);
            break;
        }
        default: {
            e = sox_create_effect(sox_find_effect("vol"));
            args[0] = p1, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
            assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
            free(e);
            __android_log_print(ANDROID_LOG_INFO, "SOX", "vol+%s",p1);
            break;
        }
    }

    /* 输出到文件的效果器 */
    e = sox_create_effect(sox_find_effect("output"));
    args[0] = (char *)out, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);
    //让整个效果器运行起来，直到遇到eof then clean
    sox_flow_effects(chain, NULL, NULL);
    sox_delete_effects_chain(chain);
    sox_close(out);
    sox_close(in);
    sox_quit();
    //释放资源
    (*env)->ReleaseByteArrayElements(env, byte_array, (jbyte *)input_pcmdata, JNI_COMMIT);
    (*env)->ReleaseStringUTFChars(env,v1,p1);
    (*env)->ReleaseStringUTFChars(env,v2,p2);
    (*env)->ReleaseStringUTFChars(env,v3,p3);
    (*env)->ReleaseStringUTFChars(env,v4,p4);
    jbyteArray jbyteArray = (*env)->NewByteArray(env, len1);//申明数组，与char字符长度一致
    (*env)->SetByteArrayRegion(env, jbyteArray, 0, len1, (jbyte *)output_pcmdata);//赋值到jbyteArray
    free(input_pcmdata);
    free(output_pcmdata);
    return jbyteArray;
}

char pos1[100],pos2[100];

void makePosition(int ntype,int steps){
    memset(pos1,0,sizeof(pos1));
    memset(pos2,0,sizeof(pos2));

}

JNIEXPORT jbyteArray JNICALL Java_com_psyone_sox_SoxProgramHandler_ConvertByPcmData(JNIEnv *env, jclass clazz,
                                                                                    jbyteArray byte_array,  jint ntype, jint steps){


    if(ntype == -1){
        return byte_array;
    }

    int len = (*env)->GetArrayLength(env, byte_array);
    int len1 = len;
    char* input_pcmdata = (char*) (*env)->GetByteArrayElements(env, byte_array, NULL);
    char* output_pcmdata;

    static sox_format_t *out, *in;
    assert(sox_init() == SOX_SUCCESS);
    assert(in = sox_open_mem_read(input_pcmdata, len, NULL, NULL, "wav"));
    assert(out = sox_open_memstream_write(&output_pcmdata, &len, &in->signal, NULL, "wav", NULL));

    sox_effects_chain_t *chain;
    sox_effect_t *e;
    char *args[10];
    chain = sox_create_effects_chain(&in->encoding, &out->encoding);
    e = sox_create_effect(sox_find_effect("input"));
    args[0] = (char *) in, assert(sox_effect_options(e, 1, args) == SOX_SUCCESS);
    assert(sox_add_effect(chain, e, &in->signal, &in->signal) == SOX_SUCCESS);
    free(e);

    e = sox_create_effect(sox_find_effect("remix"));
    makePosition(ntype,steps);
    args[0] = pos1, args[1] = pos2, assert(sox_effect_options(e, 2, args) == SOX_SUCCESS);
    assert(sox_add_effect(chain, e, &in->signal, &out->signal) == SOX_SUCCESS);
    free(e);

    //让整个效果器运行起来，直到遇到eof then clean
    sox_flow_effects(chain, NULL, NULL);
    sox_delete_effects_chain(chain);
    sox_close(out);
    sox_close(in);
    sox_quit();
    //释放资源
    (*env)->ReleaseByteArrayElements(env, byte_array, (jbyte *)input_pcmdata, JNI_COMMIT);
    jbyteArray jbyteArray = (*env)->NewByteArray(env, len1);//申明数组，与char字符长度一致
    (*env)->SetByteArrayRegion(env, jbyteArray, 0, len1, (jbyte *)output_pcmdata);//赋值到jbyteArray
    free(input_pcmdata);
    free(output_pcmdata);
    return jbyteArray;
}