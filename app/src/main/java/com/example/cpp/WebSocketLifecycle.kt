package com.example.cpp

import android.text.Editable
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.musongzi.comment.ExtensionMethod.liveSaveStateObserver
import com.musongzi.comment.ExtensionMethod.saveStateChange
import com.musongzi.core.base.business.itf.IChatter
import com.musongzi.core.base.business.itf.WebSocketEngine
import com.musongzi.core.itf.ISaveStateHandle
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.util.concurrent.TimeUnit

/**
create by linhui , data = 2022/12/7 14:59
 **/
class WebSocketLifecycle private constructor(val savedStateHandle: ISaveStateHandle) :
    DefaultLifecycleObserver,IChatter,
    WebSocketEngine {


    private var webSocketClient: MyWebSocket? = MyWebSocket(URI_STRING, savedStateHandle)


    override fun onDestroy(owner: LifecycleOwner) {
        if(webSocketClient?.isOpen == true) {

            webSocketClient?.closeBlocking()
        }
    }

    internal class MyWebSocket(uri: URI, val savedStateHandle: ISaveStateHandle) :
        WebSocketClient(uri) {
        override fun onOpen(handshakedata: ServerHandshake?) {
            Log.i(TAG, "onOpen: $handshakedata")
            OPEN_KEY.saveStateChange<ServerHandshake?>(savedStateHandle, handshakedata)
        }

        override fun onMessage(message: String?) {
            Log.i(TAG, "onMessage: $message")
            MESSAGE_KEY.saveStateChange<String?>(savedStateHandle, message)
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            Log.i(TAG, "onClose: code = $code , reason = $reason , remote = $remote")
            CLOSE_KEY.saveStateChange<Pair<Int,String?>>(savedStateHandle, code to reason)
        }

        override fun onError(ex: Exception?) {
            Log.i(TAG, "onError: $ex")
            ERROR_KEY.saveStateChange<Exception?>(savedStateHandle, ex)
        }


    }

    companion object {

        const val OPEN_KEY = "open_k"
        const val MESSAGE_KEY = "msg_k"
        const val CLOSE_KEY = "close_k"
        const val ERROR_KEY = "error_k"
        val URI_STRING = URI.create("ws://192.168.1.106:8080/ws")

        fun LifecycleOwner.registerWebSocket(savedStateHandle: ISaveStateHandle): WebSocketEngine {
            return WebSocketLifecycle(savedStateHandle).apply {
                this@registerWebSocket.lifecycle.addObserver(this)
            }
        }


    }

    override fun connect() {
        if (webSocketClient?.isOpen != true) {
            webSocketClient?.connectBlocking(8, TimeUnit.SECONDS)
        }
    }

    override fun observerOpen(lifecycleOwner: LifecycleOwner,observer: Observer<Any>) {
        OPEN_KEY.liveSaveStateObserver(lifecycleOwner,savedStateHandle,observer)
    }


    override fun observerOnClose(lifecycleOwner: LifecycleOwner,observer: Observer<Pair<Int, String?>>) {
        CLOSE_KEY.liveSaveStateObserver(lifecycleOwner,savedStateHandle,observer)
    }

    override fun observerOnMessage(lifecycleOwner: LifecycleOwner,observer: Observer<String?>) {
        MESSAGE_KEY.liveSaveStateObserver(lifecycleOwner,savedStateHandle,observer)
    }

    override fun observerOnError(lifecycleOwner: LifecycleOwner,observer: Observer<Exception?>) {
        ERROR_KEY.liveSaveStateObserver(lifecycleOwner,savedStateHandle,observer)
    }

    override fun getChatter(): IChatter {
        return this
    }

    override fun sendMsg(msg: CharSequence?) {
        if(webSocketClient?.isOpen == true) {
            when(msg){
                is String->{
                    webSocketClient?.send(msg)
                }
                is StringBuilder->{

                }
                is Editable->{

                }
                is StringBuffer->{

                }
                else ->{

                }
            }
        }
    }

    override fun sendMsg(msgInt: Int) {
        if(webSocketClient?.isOpen == null){
            webSocketClient?.send(msgInt.toString().toByteArray())
        }
    }

    override fun identification(): String {
        return "main"
    }

}