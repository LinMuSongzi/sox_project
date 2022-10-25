package com.example.cpp

import android.app.Application

class SoxApplication: Application() {



    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object{
        lateinit var INSTANCE:Application
    }

}