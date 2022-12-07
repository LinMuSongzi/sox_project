package com.example.cpp

import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.FragmentActivity

/**
create by linhui , data = 2022/12/7 16:00
 **/
class WebSocketActivity : FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        supportFragmentManager.beginTransaction().add(R.id.id_content_layout,WebSocketTestFragment()).commitNow()
    }




}