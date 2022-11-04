package com.example.cpp

import android.widget.Toast
import androidx.annotation.Nullable
import com.example.cpp.FileUtil.copyFile
import com.psyone.sox.EffectsBean
import com.musongzi.core.util.ActivityThreadHelp
import java.io.File

@Deprecated("已过期只是测试")
object SoxUtil {


    const val TAG = "SOX_UTIL"
    val FILE_PARENT = ActivityThreadHelp.getCurrentApplication().cacheDir
    val FILE_MP3 = File(FILE_PARENT, "qsws.wav")

    @Nullable
    fun buildMusicByEffectInfo(effectsBean: EffectsBean?, byteArray: ByteArray):ByteArray{
       return buildMusicByEffectInfo(effectsBean?.r_name,effectsBean?.values?.get(0),byteArray)
    }

    @JvmStatic
    @Nullable
    external fun buildMusicByEffectInfo(effectRealName: String?,values: String?,byteArray: ByteArray):ByteArray

    @Nullable
    fun buildMusicByEffectInfoFile(effectsBean: EffectsBean?, path:String, byteArray: ByteArray):Int{
        return buildMusicByEffectInfoFile(effectsBean?.r_name, effectsBean?.values?.get(0),path,effectsBean?.charParams,byteArray)
    }

//    @JvmStatic
//    @Nullable
//    external fun buildMusicByEffectInfoFile(effectRealName: String?,values:Array<String>?,path:String,charArray: CharArray?,byteArray: ByteArray):Int

    @JvmStatic
    @Nullable
    external fun buildMusicByEffectInfoFile(effectRealName: String?,values:String?,path:String,charArray: CharArray?,byteArray: ByteArray):Int


    fun copy(mp3: String, file: File) {
        copyFile(File(mp3), file)
    }

    fun show(s: String) {
        Toast.makeText(ActivityThreadHelp.getCurrentApplication(),s,Toast.LENGTH_SHORT).show()
    }


}