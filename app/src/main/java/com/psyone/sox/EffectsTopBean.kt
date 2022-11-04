package com.psyone.sox

import android.os.Parcelable
import androidx.annotation.Keep
import com.musongzi.core.base.bean.BaseChooseBean
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class EffectsTopBean(
    val c_name: String?,
    val content: String?,
    val e_name: String?,
    val r_name:String?,
    val e_child: List<EffectsBean>? = null,
    var isChildChoose:Boolean = false
) : Parcelable, BaseChooseBean() {


    constructor(c_name: String,e_child: List<EffectsBean>):this(c_name,null,null,null,e_child = e_child)


    override fun isChoose(): Boolean {
        return super.isChoose()
    }

    override fun choose(b: Boolean) {
        if (e_child != null && e_child.isNotEmpty()) {
            super.choose(b)
        }
    }
}