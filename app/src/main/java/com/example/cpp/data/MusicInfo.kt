package com.example.cpp.data

class MusicInfo {
    var simpleRate: Int = 0
    var bit: Int = 0
    var channel: Int = 0
    var timeSecond: Long = 0
    var headBitSize = 44
    override fun toString(): String {
        return "MusicInfo(simpleRate=$simpleRate, bit=$bit, channel=$channel, timeSecond=$timeSecond, headBitSize=$headBitSize)"
    }


    private var dataSize: Long? = null


    fun getDataSize():Long =
        if (simpleRate == 0) 0
        else {
            if (dataSize == null) (simpleRate * channel * bit / 8L).let {
                dataSize = it
                it
            } else dataSize!!
        }


}