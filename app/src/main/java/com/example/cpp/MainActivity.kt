package com.example.cpp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.pm.PermissionInfoCompat
import com.example.cpp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.idPlayText.text =
            "exo播放音乐"//SoxUtil.subtraction(4321, 1234).toString()//stringFromJNI()
        binding.idPlayText.setOnClickListener {
            SoxUtil.exoPlaySImple(this,binding.idPlayView)
//            SoxUtil.exeuteComment("soxi ${SoxUtil.BASE_URL}/千山万水.mp3")
        }

        val launch = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                binding.idBuildMusicText.setOnClickListener {
                    try {
                        SoxUtil.buildMusic()
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

        init {
            System.loadLibrary("msz_sox")
            val state = SoxUtil.initSox()
            Log.i(TAG, ": SoxUtil ini $state")
//            System.loadLibrary("msz")
        }
    }


}