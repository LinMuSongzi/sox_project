package com.example.cpp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.cpp.business.IEquBusiness
import com.example.cpp.data.EuqInfo
import com.example.cpp.databinding.FragmentParaBinding
import com.example.cpp.vm.MusicEffectsViewModel
import com.musongzi.core.ExtensionCoreMethod.topInstance
import com.musongzi.core.base.fragment.DataBindingFragment
import com.musongzi.core.itf.holder.IHolderViewModelProvider

class ParaFragment : DialogFragment(), IHolderViewModelProvider, IEquBusiness {


    private val mViewModel: MusicEffectsViewModel?
        get() = MusicEffectsViewModel::class.java.topInstance(this@ParaFragment)

    private lateinit var databinding: FragmentParaBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.let {
            databinding = FragmentParaBinding.inflate(layoutInflater, container, false)
            databinding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel?.euqInfoThis?.value?.apply {
            databinding.bean = this
        }
        databinding.business = this
    }


    override fun topViewModelProvider(): ViewModelProvider? {
        return ViewModelProvider(requireActivity())
    }

    override fun thisViewModelProvider() = null

    override fun agreeClick() {
        if (databinding.idValue1.text.isEmpty()
            || databinding.idValue2.text.isEmpty()
            || databinding.idValue3.text.isEmpty()
            || databinding.idTitle.text.isEmpty()
        ) {
            Toast.makeText(requireContext(), "请输入完整参数", Toast.LENGTH_SHORT).show()
        } else {
            mViewModel?.euqInfoThis?.value = EuqInfo(
                databinding.idTitle.text.toString(),
                databinding.idValue1.text.toString(),
                databinding.idValue2.text.toString(),
                databinding.idValue3.text.toString(),
                databinding.idValue4.text.toString()
            )
            dismissAllowingStateLoss()
        }
    }

    override fun resetClick() {
        databinding.bean = EuqInfo("","","","","")
    }


}