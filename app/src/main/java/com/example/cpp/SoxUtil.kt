package com.example.cpp

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.cpp.FileUtil.copyFile
import com.psyone.sox.SoxAudioProcessor
import com.example.cpp.data.EffectsBean
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem.fromUri
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.audio.AudioCapabilities
import com.google.android.exoplayer2.audio.AudioSink
import com.google.android.exoplayer2.audio.DefaultAudioSink
import com.musongzi.core.base.vm.DataDriveViewModel
import com.musongzi.core.util.ActivityThreadHelp
import java.io.File

object SoxUtil {

    val BASE_URL: String = "http://192.168.1.106:8080"
    const val TAG = "SOX_UTIL"
    val FILE_PARENT = ActivityThreadHelp.getCurrentApplication().cacheDir
    val FILE_MP3 = File(FILE_PARENT, "qsws.wav")

    @Nullable
    fun buildMusicByEffectInfo(effectsBean: EffectsBean?,byteArray: ByteArray):ByteArray{
       return buildMusicByEffectInfo(effectsBean?.r_name,effectsBean?.values?.get(0),byteArray)
    }

    @JvmStatic
    @Nullable
    external fun buildMusicByEffectInfo(effectRealName: String?,values: String?,byteArray: ByteArray):ByteArray

    @Nullable
    fun buildMusicByEffectInfoFile(effectsBean: EffectsBean?,path:String,byteArray: ByteArray):Int{
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