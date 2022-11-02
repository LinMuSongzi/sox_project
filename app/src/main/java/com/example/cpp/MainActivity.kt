package com.example.cpp

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.cpp.SoxUtil.exoPlaySImple
import com.example.cpp.array.ByteArrayEngine
import com.example.cpp.array.ByteArrayEngine.Companion.PATH_KEY
import com.example.cpp.business.SoudSoxBusiness
import com.example.cpp.data.EffectsBean
import com.example.cpp.databinding.ActivityMainBinding
import com.example.cpp.vm.MusicEffectsViewModel
import com.musongzi.comment.ExtensionMethod.convertFragment
import com.musongzi.comment.ExtensionMethod.instance
import com.musongzi.comment.ExtensionMethod.instanceByVm
import com.musongzi.comment.ExtensionMethod.liveSaveStateObserver
import com.musongzi.comment.activity.MszFragmentActivity
import com.musongzi.core.itf.INotifyDataSetChanged
import java.io.File

class MainActivity : MszFragmentActivity(), INotifyDataSetChanged {


    private lateinit var binding: ActivityMainBinding
//    var soxBusiness: SoudSoxBusiness? = null

    var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setChildMainView(binding.root)
        MusicEffectsViewModel::class.java.instance(business.topViewModelProvider())?.apply {
            runOnUiThread {
                MusicEffectsViewModel.CHOOSE_EFFECY_KEY.liveSaveStateObserver<EffectsBean>(this) {
                    binding.chooseBinding.bean = it
                }
                getHolderBusiness().buildRecycleMusicEffectsData(binding.idRecyclerView)
                loaderEffectsData()
            }
        }

//        showFragmentByte()

        // Example of a call to a native method
        binding.idPlayText.text =
            "exo播放音乐"//SoxUtil.subtraction(4321, 1234).toString()//stringFromJNI()


        val launch = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                if (SoxUtil.FILE_MP3.exists()) {
                    SoxUtil.FILE_MP3.delete()
                }
//                binding.idBuildMusicText.setOnClickListener {
//
//                }

                binding.idPlayText.setOnClickListener {


                    exoPlaySImple(MusicEffectsViewModel::class.java.instance(business.topViewModelProvider())!!,"${BASE_URL}wavTest2.mp3")

//                    SoudSoxBusiness::class.java.instanceByVm(
//                        MusicEffectsViewModel::class.java,
//                        business.topViewModelProvider()
//                    )?.observer(musicPath)
                }
            }
        }
        launch.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun showFragmentByte() {
        if (fragment == null) {
            binding.idByteLayout.visibility = View.VISIBLE
            fragment = ByteArrayEngine::class.java.convertFragment(Bundle().apply {
                putString(PATH_KEY, musicPath)
            })
            supportFragmentManager.beginTransaction().replace(
                R.id.id_byte_layout,
                fragment!!
            ).commitNow()
        } else {
            val fragment = this.fragment!!
            if (fragment.isHidden) {
                supportFragmentManager.beginTransaction().show(fragment).commitNow()
            } else {
                supportFragmentManager.beginTransaction().hide(fragment).commitNow()
            }
        }
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
            Environment.getExternalStorageDirectory().absolutePath + File.separator + "ad7d1d4edff2167163b7303f0fd9f369.wav"
//            Environment.getExternalStorageDirectory().absolutePath + File.separator + "dnsRXV0SUH6ASVysADygTuw80Ak462.wav"
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun notifyDataSetChanged() {
        binding.idRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun notifyDataSetChangedItem(postiont: Int) {

    }

    override fun showDialog(msg: String?) {

    }

    override fun disimissDialog() {

    }


}