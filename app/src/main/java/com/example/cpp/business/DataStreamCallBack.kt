package com.example.cpp.business

import androidx.lifecycle.LifecycleOwner
import com.example.cpp.data.MusicInfo

interface DataStreamCallBack<T> {

    fun initWork(path:String)

    fun initWork(musicInfo: MusicInfo)

    fun operateStream():ByteArray?

    fun getLifecycleOwner():LifecycleOwner?


}