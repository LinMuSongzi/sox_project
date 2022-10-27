package com.example.cpp.data.reqeust

import androidx.annotation.ColorRes

data class RequestInfo<T>  (var token: String? = null, var body: T? = null) {
}


fun <T> String?.buildRequestInfo(t: T?): RequestInfo<T> {
    return RequestInfo(this, t);
}