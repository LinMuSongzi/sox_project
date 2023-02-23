package com.example.cpp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.cpp.databinding.FragmenChooseMusicTypeBinding
import com.example.cpp.vm.MusicEffectsViewModel
import com.musongzi.core.ExtensionCoreMethod.topInstance
import com.musongzi.core.base.fragment.DataBindingFragment
import com.musongzi.core.itf.holder.IHolderViewModelProvider

class ChooseMusicStyleFragment : BaseDialogFragment<FragmenChooseMusicTypeBinding>() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.let {
            dataBinding = FragmenChooseMusicTypeBinding.inflate(it, container, false)

            dataBinding.root
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding.idAgreeBtn.setOnClickListener {
            MusicEffectsViewModel::class.java.topInstance(this)?.musicTypeFlow?.value = dataBinding.idEditEt.text.toString()
            dismissAllowingStateLoss()
        }
    }


}