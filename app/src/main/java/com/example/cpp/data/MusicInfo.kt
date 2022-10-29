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


}