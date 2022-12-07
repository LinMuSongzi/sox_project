package com.example.cpp

import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableField
import com.example.cpp.WebSocketLifecycle.Companion.registerWebSocket
import com.example.cpp.databinding.FragmentWebSocketTestBinding
import com.musongzi.core.ExtensionCoreMethod.thisInstance
import com.musongzi.core.base.business.itf.WebSocketEngine
import com.musongzi.core.base.fragment.DataBindingFragment
import com.musongzi.core.base.vm.SimpleViewModel

/**
create by linhui , data = 2022/12/7 15:33
 **/
class WebSocketTestFragment : DataBindingFragment<FragmentWebSocketTestBinding>() {

    private lateinit var mWebSocketEngine: WebSocketEngine

    var receiverText = ObservableField<String?>()

    override fun initView() {
        dataBinding.bean = this
        mWebSocketEngine = registerWebSocket(SimpleViewModel::class.java.thisInstance(this)!!.localSavedStateHandle())
    }

    override fun initEvent() {
        dataBinding.idActionBtn.setOnClickListener {
            mWebSocketEngine.connect()
        }
        mWebSocketEngine.observerOpen(this){
            dataBinding.idEdittext.isEnabled = true
            Toast.makeText(requireContext(), "webSocket连接成功,你可以发消息了", Toast.LENGTH_SHORT).show()
            dataBinding.idActionBtn.text = "webSocket连接成功,你可以发消息了"
            dataBinding.idActionBtn.setOnClickListener {
                mWebSocketEngine.sendMsg(dataBinding.idEdittext.text.toString().trim())
            }
        }
        mWebSocketEngine.observerOnError(this){
            Toast.makeText(requireContext(), "webSocket连接错误 , ${it?.message} ", Toast.LENGTH_SHORT).show()
            dataBinding.idEdittext.isEnabled = false
        }
        mWebSocketEngine.observerOnClose(this){
            Toast.makeText(requireContext(), "webSocket连接已经关闭", Toast.LENGTH_SHORT).show()
        }

        mWebSocketEngine.observerOnMessage(this){
            Log.i(TAG, "initEvent: text = $it")
            receiverText.set(it)
        }

    }

    override fun initData() {

    }
}