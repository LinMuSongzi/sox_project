package com.example.cpp.vm

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.cpp.api.Api
import com.example.cpp.business.MusicEffectsBusiness
import com.musongzi.comment.ExtensionMethod.liveSaveStateObserver
import com.musongzi.comment.viewmodel.ApiViewModel
import com.musongzi.core.ExtensionCoreMethod.sub
import com.musongzi.core.base.vm.MszViewModel
import com.musongzi.core.itf.IClient
import com.musongzi.core.itf.INotifyDataSetChanged
import com.psyone.sox.SoxProgramHandler.EFFECTS_ARRAY

class MusicEffectsViewModel : ApiViewModel<INotifyDataSetChanged, MusicEffectsBusiness, Api>() {

    companion object {
        const val CHOOSE_EFFECY_KEY = "e_key"
    }


    fun loaderEffectsData() {
        getApi().effects.sub { ef ->

            (getHolderBusiness().realData() as? ArrayList)?.let {
//                Log.i(TAG, "loaderEffectsData: ${ef.data?.get(0)?.cName}")
                it.clear()
                it.addAll(ef.data!!)
                getHolderClient()
            }?.notifyDataSetChanged()
        }

//        (getHolderBusiness().realData() as? ArrayList)?.let {
//            it.clear()
//            it.addAll(EFFECTS_ARRAY)
//            getHolderClient()
//        }?.notifyDataSetChanged()


    }

}