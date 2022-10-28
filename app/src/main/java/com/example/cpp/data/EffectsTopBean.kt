package com.example.cpp.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.musongzi.core.ExtensionCoreMethod.dataBindingInflate
import com.musongzi.core.base.bean.BaseChooseBean
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class EffectsTopBean(
    @SerializedName("c_name")
    val cName: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("e_name")
    val eName: String?,
    val r_name:String?,
    @SerializedName("e_child")
    val childs: List<EffectsBean>? = null,
    var isChildChoose:Boolean = false
) : Parcelable, BaseChooseBean() {



    override fun isChoose(): Boolean {
        return super.isChoose()
    }

    override fun choose(b: Boolean) {
        if (childs != null && childs.isNotEmpty()) {
            super.choose(b)
        }
    }
}