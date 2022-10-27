package com.example.cpp.data.response
import android.os.Parcelable


import androidx.annotation.Keep
import com.example.cpp.data.EffectsBean

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Keep
@Parcelize
data class EffectDatasRespones(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val `data`: List<EffectsBean?>?,
    @SerializedName("msg")
    val msg: String?
) : Parcelable

