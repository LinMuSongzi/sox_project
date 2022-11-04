package com.example.cpp

import android.content.pm.PackageManager
import android.util.Log
import com.musongzi.core.base.MszApplicaton
import com.musongzi.core.base.manager.ManagerInstanceHelp
import com.musongzi.core.base.manager.RetrofitManager
import com.musongzi.core.util.ActivityThreadHelp

val BASE_URL = ActivityThreadHelp.getCurrentApplication().let {
    val info = it.packageManager.getApplicationInfo(it.packageName, PackageManager.GET_META_DATA)
    val requets = info.metaData?.getString("REQUEST_URL")
    Log.i(TAG, "sox requets = $requets")
    requets!!
}

const val TAG = "SoxApplication"

class SoxApplication: MszApplicaton() {


    override fun getManagers(): Array<ManagerInstanceHelp>? = arrayOf(ManagerInstanceHelp.instanceOnReady{
        RetrofitManager.getInstance().init {
            BASE_URL
        }
    })


}