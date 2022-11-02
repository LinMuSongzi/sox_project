package com.example.cpp.business

interface ISteamConsumption {

    fun initHandler()

    fun isReady(): Boolean

    fun handler(byteArray: ByteArray, offset: Int, length: Long)

    companion object{

        const val NORMAL_STATE = 0
        const val INIT_PLAYER_STATE = 0x1
        const val READY_STATE = 0x2
        const val RUNNING_STATE = 0x10
        const val END_STATE = 0x20

        const val MASK_STATE = READY_STATE.or(RUNNING_STATE).or(END_STATE).or(INIT_PLAYER_STATE)

    }

}