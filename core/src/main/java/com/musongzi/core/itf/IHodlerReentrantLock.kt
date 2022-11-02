package com.musongzi.core.itf

import java.util.concurrent.locks.ReentrantLock

interface IHodlerReentrantLock {

    fun getHolderReentrantLock():ReentrantLock

}