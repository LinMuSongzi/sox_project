package com.musongzi.core.base.map

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.musongzi.core.itf.ISaveStateHandle

class SaveStateHandleWarp(private val stateHandle: SavedStateHandle) : ISaveStateHandle {
    override fun <T> getLiveData(key: String): MutableLiveData<T> {
        return stateHandle.getLiveData(key)
    }

    override fun <T> getLiveData(key: String, initialValue: T?): MutableLiveData<T> {
        return stateHandle.getLiveData(key, initialValue)
    }

    override fun contains(key: String): Boolean {
        return stateHandle.contains(key)
    }

    override fun keys(): Set<String?> {
        return stateHandle.keys()
    }

    override fun <T> get(key: String): T? {
        return stateHandle.get(key)
    }

    override fun <T> remove(key: String): T? {
        return stateHandle.remove(key)
    }

    override fun <T> set(key: String, value: T?) {
        if(Thread.currentThread() != Looper.getMainLooper().thread){
            Handler(Looper.getMainLooper()).post{
                stateHandle.set(key, value)
            }
            return
        }
        stateHandle.set(key, value)
    }


}