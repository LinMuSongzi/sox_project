package com.example.cpp

import android.app.Application
import androidx.multidex.MultiDexApplication

class SoxApplication: MultiDexApplication() {



    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object{
        lateinit var INSTANCE:Application
    }

}