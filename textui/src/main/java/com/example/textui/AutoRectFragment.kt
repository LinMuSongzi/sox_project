package com.example.textui

import com.example.textui.databinding.FragemntRectBinding
import com.musongzi.core.base.fragment.DataBindingFragment

class AutoRectFragment : DataBindingFragment<FragemntRectBinding>() {
    override fun initView() {
        RectBusiness().initRecycleView(dataBinding.idRecyclerView)
    }

    override fun initEvent() {

    }

    override fun initData() {

    }





}