package com.psyone.sox

object SoxProgramHandler {

    init {
        System.loadLibrary("ColSleep_sox_lib")
    }

    const val TAG = "SOX"

    @JvmStatic
    external fun exampleConvertByPcmData(byteArray: ByteArray,values: String?):ByteArray


}