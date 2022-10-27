package com.example.cpp.vm

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.cpp.business.MusicEffectsBusiness
import com.musongzi.comment.ExtensionMethod.liveSaveStateObserver
import com.musongzi.core.base.vm.MszViewModel
import com.musongzi.core.itf.IClient

class MusicEffectsViewModel: MszViewModel<IClient, MusicEffectsBusiness>() {


    override fun putArguments(d: Bundle?) {
        super.putArguments(d)
        getThisLifecycle()?.apply {
            localSavedStateHandle().getLiveData<RecyclerView>(RECYCLE_UPDATE_KEY).observe(this){

            }
        }

    }


    companion object{

        const val RECYCLE_UPDATE_KEY = "recycle"

    }


}