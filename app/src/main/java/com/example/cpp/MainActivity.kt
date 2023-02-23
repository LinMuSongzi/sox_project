package com.example.cpp

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.cpp.array.ByteArrayEngine
import com.example.cpp.array.ByteArrayEngine.Companion.PATH_KEY
import com.psyone.sox.EffectsBean
import com.example.cpp.databinding.ActivityMainBinding
import com.example.cpp.databinding.AdapterByteShowBinding
import com.example.cpp.fragment.ChooseMusicStyleFragment
import com.example.cpp.fragment.ParaFragment
import com.example.cpp.vm.MusicEffectsViewModel
import com.google.android.exoplayer2.Player
import com.musongzi.comment.ExtensionMethod.convertFragment
import com.musongzi.comment.ExtensionMethod.instance
import com.musongzi.comment.ExtensionMethod.liveSaveStateObserver
import com.musongzi.comment.activity.MszFragmentActivity
import com.musongzi.comment.bean.FileBean
import com.musongzi.comment.util.setText
import com.musongzi.core.ExtensionCoreMethod.adapter
import com.musongzi.core.ExtensionCoreMethod.linearLayoutManager
import com.musongzi.core.base.map.LocalSavedHandler
import com.musongzi.core.itf.INotifyDataSetChanged
import com.musongzi.core.itf.ISaveStateHandle
import com.musongzi.core.itf.page.ISource
import com.psyone.sox.NewHandlerSoxAudioProcessor
import com.psyone.sox.SoxProgramHandler
import com.psyone.sox.SoxProgramHandler.exoPlaySImple
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : MszFragmentActivity(), INotifyDataSetChanged,
    ISaveStateHandle by LocalSavedHandler() {


    private val CHOOSE_TAG_F = "ChooseMusicStyleFragment"
    private val PARA_TAG_F: String = "ParaFragment"
    private val mNewHandlerSoxAudioProcessor = NewHandlerSoxAudioProcessor()
    private var player: Player? = null
    private lateinit var binding: ActivityMainBinding

    private val modeLiveData = MutableLiveData(0)

    var fragment: Fragment? = null

    private var playMusicPosition: Int = -1
        set(value) {
            playMusic(field, value)
            if (field != value) {
                runOnUiThread {
                    notifyDataSetChanged()
                }
            }
            field = value

        }

    private fun playMusic(last: Int, newMusic: Int) {
        Log.i(TAG, "playMusic: last = $last , newMusic = $newMusic")


        val p: Player = player ?: exoPlaySImple(this, this, list.realData()[newMusic].path, mNewHandlerSoxAudioProcessor)!!
        if (player == null) {
            player = p
        } else {
            if (last == newMusic) {
                if (player?.isPlaying == true) {
                    player?.pause()
                } else {
                    player?.play()
                }
                return
            }
            player?.apply {
                pause()
                stop()
                release()
            }
            player = null
            playMusic(last, newMusic)
        }


    }

    private val list = object : ISource<FileBean> {

        val list = mutableListOf<FileBean>().apply {
            add(FileBean(千山万水_mp3))
            add(FileBean(dnsRXV0SUH6ASVysADygTuw80Ak462_wav))
        }

        override fun realData() = list

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setChildMainView(binding.root)


        binding.idPlayRecyclerView.linearLayoutManager {
            list.adapter(AdapterByteShowBinding::class.java, { d, i, p ->
                setText(d.idText, i.path)

                if (p == playMusicPosition) {
                    d.idText.setTextColor(Color.RED)
                } else {
                    d.idText.setTextColor(Color.parseColor("#999999"))
                }
                d.root.setOnClickListener {
                    Log.i(TAG, "playMusicPosition: $p")
                    playMusicPosition = p


                }
            }, false)
        }

        Log.i(
            TAG,
            "onCreate: createAudioWorkConetxt = ${
                SoxProgramHandler.createAudioWorkConetxt(
                    this,
                    0
                )
            }"
        )

        binding.idPlayText.text =
            "exo播放音乐"//SoxUtil.subtraction(4321, 1234).toString()//stringFromJNI()


        val launch = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                if (SoxUtil.FILE_MP3.exists()) {
                    SoxUtil.FILE_MP3.delete()
                }
                binding.idBuildMusicText.setOnClickListener {
//                    mNewHandlerSoxAudioProcessor.musicEffecyBean = null
                    if (modeLiveData.value == 0) {
                        modeLiveData.value = 1
                    } else {
                        modeLiveData.value = 0
                    }
                }

                binding.idPlayText.setOnClickListener {
                    if (list.realData().isNotEmpty()) {
                        playMusicPosition = 0
                    }
                }

                MusicEffectsViewModel::class.java.instance(business.topViewModelProvider())?.apply {
                    runOnUiThread {
                        MusicEffectsViewModel.CHOOSE_EFFECY_KEY.liveSaveStateObserver<EffectsBean?>(
                            this
                        ) { bean ->
                            bean?.apply {
                                binding.chooseBinding.bean = this
                            }
                            mNewHandlerSoxAudioProcessor.musicEffecyBean = bean
                        }
                        getHolderBusiness().buildRecycleMusicEffectsData(binding.idRecyclerView)
                        loaderEffectsData();
                    }

                    euqInfoThis.observe(this@MainActivity) { info ->

                        info?.apply {
                            mNewHandlerSoxAudioProcessor.euqInfo = this
                        }
                    }

                    lifecycleScope.launch {
                        musicTypeFlow.collect{m->
                            mNewHandlerSoxAudioProcessor.musicType = m.trim()
                        }
                    }

                }

                binding.idSetTypeBtn.setOnClickListener {
                    ChooseMusicStyleFragment().show(supportFragmentManager, CHOOSE_TAG_F)
                }

                binding.idSetParaBtn.setOnClickListener {
                    ParaFragment().show(supportFragmentManager, PARA_TAG_F)
                }




            }


        }
        launch.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)


        modeLiveData.observe(this) {
            if (it == 0) {
                binding.idBuildMusicText.text = "当前为原声"
            } else if (it == 1) {
                binding.idBuildMusicText.text = "处理后声音"
            }
        }
        modeLiveData.observe(this) {
            mNewHandlerSoxAudioProcessor.isNativeMusic = it == 0
        }
    }


    private fun showFragmentByte() {
        if (fragment == null) {
            binding.idByteLayout.visibility = View.VISIBLE
            fragment = ByteArrayEngine::class.java.convertFragment(Bundle().apply {
                putString(PATH_KEY, 千山万水_mp3)
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
        val 千山万水_mp3 = "file:///android_asset/" + "千山万水.mp3"
        val dnsRXV0SUH6ASVysADygTuw80Ak462_wav = "file:///android_asset/" + "dnsRXV0SUH6ASVysADygTuw80Ak462.wav"
        //Environment.getExternalStorageDirectory().absolutePath + File.separator + "千山万水.mp3"
//            Environment.getExternalStorageDirectory().absolutePath + File.separator + "ad7d1d4edff2167163b7303f0fd9f369.wav"
//            Environment.getExternalStorageDirectory().absolutePath + File.separator + "dnsRXV0SUH6ASVysADygTuw80Ak462.wav"


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun notifyDataSetChanged() {
        binding.idPlayRecyclerView.adapter?.notifyDataSetChanged()
        binding.idRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun notifyDataSetChangedItem(postiont: Int) {

    }

    override fun showDialog(msg: String?) {

    }

    override fun disimissDialog() {

    }


}