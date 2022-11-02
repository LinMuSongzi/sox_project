package com.example.cpp.business

import java.io.InputStream

interface ISoudPlayBusiness {
    fun observer(p: String)
    fun observer(inputStreamMethod: (() -> InputStream?)? = null)

}
