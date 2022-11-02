package com.example.cpp.business

import android.media.AudioTrack

class SimpleSteamConsumption(sound: ISoundFactoryCollaborator) : ISteamConsumption {




    var audioTrack:AudioTrack? = null
    override fun initHandler() {
        TODO("Not yet implemented")
    }


//    override fun initHandler() {
//        checkAudioTrack
//    }

    override fun isReady(): Boolean {
        TODO("Not yet implemented")
    }

    override fun handler(byteArray: ByteArray, offset: Int, length: Long) {
        TODO("Not yet implemented")
    }

}
