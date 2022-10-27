package com.example.cpp

import android.Manifest
import android.app.backup.FileBackupHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.cpp.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var soxBusiness: SoudSoxBusiness? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        setContentView(binding.root)
        // Example of a call to a native method
        binding.idPlayText.text =
            "exo播放音乐"//SoxUtil.subtraction(4321, 1234).toString()//stringFromJNI()
        binding.idPlayText.setOnClickListener {
//            if (soxBusiness == null) {
//                soxBusiness =  musicPath.openSoxBusinessByInputStream(this)
//            }
//            soxBusiness?.observer()
            SoxUtil.exoPlaySImple(this, binding.idPlayView,
//            Environment.getExternalStorageDirectory().absolutePath + File.separator + "ad7d1d4edff2167163b7303f0fd9f369.wav")
        SoxUtil.FILE_MP3.absolutePath)
//            SoxUtil.exeuteComment("soxi ${SoxUtil.BASE_URL}/千山万水.mp3")
        }

        val launch = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                if (SoxUtil.FILE_MP3.exists()) {
                    SoxUtil.FILE_MP3.delete()
                }
                binding.idBuildMusicText.setOnClickListener {
                    try {
//                        val file = File(cacheDir,"haha.mp3")
//                        if(file.exists() || file.length() > 0){
                        if (SoxUtil.FILE_MP3.exists()) {
                            SoxUtil.show("已经构建完成~")
                            return@setOnClickListener
                        }
                        try {
                            SoxUtil.buildMusic2(Environment.getExternalStorageDirectory().absolutePath + File.separator + "ad7d1d4edff2167163b7303f0fd9f369.wav")
                            SoxUtil.show("构建music 成功~")
                        } catch (e: Exception) {
                            SoxUtil.show("构建music 失败~~~~！！！！！！！！！！！~")
                            e.printStackTrace()
                        }

//                        }else{
//                            val mp3 = (Environment.getExternalStorageDirectory().absolutePath + File.separator + "千山万水.mp3")
//                            SoxUtil.copy(mp3,file)
//                            Log.i(TAG, "onCreate: copy succed")
//                            binding.idBuildMusicText.performClick()
//                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        launch.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    /**
     * A native method that is implemented by the 'cpp' native library,
     * which is packaged with this application.
     */
//    external fun stringFromJNI(): String
////    external fun sum(one: Int,tow:Int):Int
//
    companion object {
        // Used to load the 'cpp' library on application startup.
        private const val TAG = SoxUtil.TAG
        val musicPath =
//            Environment.getExternalStorageDirectory().absolutePath + File.separator + "ad7d1d4edff2167163b7303f0fd9f369.wav"
            Environment.getExternalStorageDirectory().absolutePath + File.separator + "dnsRXV0SUH6ASVysADygTuw80Ak462.wav"

        init {
            System.loadLibrary("msz_sox")
            val state = SoxUtil.initSox()
            Log.i(TAG, ": SoxUtil ini $state")
//            System.loadLibrary("msz")
        }
    }


}