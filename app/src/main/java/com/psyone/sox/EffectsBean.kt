package com.psyone.sox

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.musongzi.core.base.bean.BaseChooseBean
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
class EffectsBean(
    val c_name: String?,
    val content: String?,
    val e_name: String?,
    val r_name:String?,
) : Parcelable, BaseChooseBean(){

    constructor(c_name: String?,e_name: String?,content: String?):this(c_name,content,e_name,e_name)
    var values: Array<String>? = null
    var charParams:CharArray? = null

}