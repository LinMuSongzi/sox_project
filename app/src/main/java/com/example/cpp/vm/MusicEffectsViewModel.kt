package com.example.cpp.vm

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.cpp.api.Api
import com.example.cpp.business.MusicEffectsBusiness
import com.musongzi.comment.ExtensionMethod.liveSaveStateObserver
import com.musongzi.comment.viewmodel.ApiViewModel
import com.musongzi.core.ExtensionCoreMethod.sub
import com.musongzi.core.base.vm.MszViewModel
import com.musongzi.core.itf.IClient
import com.musongzi.core.itf.INotifyDataSetChanged

class MusicEffectsViewModel: ApiViewModel<INotifyDataSetChanged, MusicEffectsBusiness,Api>() {




    fun loaderEffectsData(){
        getApi().getEffects(null).sub{
            getHolderClient()?.notifyDataSetChanged()
        }
    }






    companion object{

        const val RECYCLE_UPDATE_KEY = "recycle"

    }


}