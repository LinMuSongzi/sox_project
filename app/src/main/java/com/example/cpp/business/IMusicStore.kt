package com.example.cpp.business

import io.reactivex.rxjava3.core.Observable

interface IMusicStore {

    fun getMusicPath(): Observable<String>

//    fun getMusicPaths(): Observable<T>?

}