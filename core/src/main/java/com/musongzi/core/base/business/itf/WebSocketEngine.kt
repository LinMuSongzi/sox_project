package com.musongzi.core.base.business.itf

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception

interface WebSocketEngine :InstantMessagingBusiness{

    fun connect(uri:String)
    fun connect()
    fun observerOpen(lifecycleOwner: LifecycleOwner,observer: Observer<Any>)
    fun observerOnClose(lifecycleOwner: LifecycleOwner,observer: Observer<Pair<Int,String?>>)
    fun observerOnMessage(lifecycleOwner: LifecycleOwner,observer: Observer<String?>)
    fun observerOnError(lifecycleOwner: LifecycleOwner,observer: Observer<Exception?>)

}