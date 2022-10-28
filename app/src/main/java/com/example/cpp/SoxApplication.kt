package com.example.cpp

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.musongzi.core.base.MszApplicaton
import com.musongzi.core.base.manager.InstanceManager
import com.musongzi.core.base.manager.ManagerInstanceHelp
import com.musongzi.core.base.manager.RetrofitManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.lang.reflect.Method

const val BASE_URL = "http://192.168.1.106:8080/"

class SoxApplication: MszApplicaton() {



    override fun getManagers(): Array<ManagerInstanceHelp>? = arrayOf(ManagerInstanceHelp.instanceOnReady{
        RetrofitManager.getInstance().init {
            BASE_URL
        }
    })

}