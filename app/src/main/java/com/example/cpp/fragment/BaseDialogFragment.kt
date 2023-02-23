package com.example.cpp.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.musongzi.core.itf.holder.IHolderViewModelProvider

open class BaseDialogFragment<D : ViewDataBinding> : DialogFragment(), IHolderViewModelProvider {

    protected lateinit var dataBinding: D


    override fun topViewModelProvider() = ViewModelProvider(requireActivity())

    override fun thisViewModelProvider() = ViewModelProvider(this)




}