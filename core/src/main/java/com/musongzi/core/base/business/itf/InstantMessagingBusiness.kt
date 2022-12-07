package com.musongzi.core.base.business.itf

interface InstantMessagingBusiness {

    fun getChatter():IChatter

    fun sendMsg(msg:CharSequence?)

    fun sendMsg(msgInt:Int)
}