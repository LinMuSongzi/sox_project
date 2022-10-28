package com.example.cpp.business

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.cpp.data.EffectsBean
import com.example.cpp.data.EffectsTopBean
import com.example.cpp.databinding.AdapterEffectsBinding
import com.example.cpp.databinding.AdapterEffectsDetialBinding
import com.example.cpp.vm.MusicEffectsViewModel
import com.musongzi.comment.ExtensionMethod.saveStateChange
import com.musongzi.comment.databinding.AdapterFileBinding
import com.musongzi.comment.util.SourceImpl
import com.musongzi.comment.util.setText
import com.musongzi.comment.util.showImage
import com.musongzi.comment.util.viewVisibility
import com.musongzi.core.ExtensionCoreMethod.adapter
import com.musongzi.core.ExtensionCoreMethod.linearLayoutManager
import com.musongzi.core.base.adapter.ListAbstacyAdapter
import com.musongzi.core.base.adapter.TypeSupportAdaper
import com.musongzi.core.base.adapter.TypeSupportAdaper.Companion.ZERO
import com.musongzi.core.base.business.BaseMapBusiness
import com.musongzi.core.itf.page.ISource
import com.musongzi.core.util.ScreenUtil

class MusicEffectsBusiness : BaseMapBusiness<MusicEffectsViewModel>(),
    ISource<EffectsTopBean> by SourceImpl() {

    var openBean: EffectsTopBean? = null
    var chooseBean: EffectsBean? = null

    @SuppressLint("NotifyDataSetChanged")
    fun buildRecycleMusicEffectsData(recyclerView: RecyclerView) {
        recyclerView.linearLayoutManager {
            adapter(AdapterEffectsBinding::class.java) { d, i, _ ->
                var adapter = d.idChildRecycle.adapter
                d.idMoreTv.setOnClickListener {
                    openBean?.choose(false)
                    openBean = if (i == openBean) {
                        null;
                    } else {
                        i.choose(true)

                        i
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                if (i.isChoose()) {
                    if (adapter == null) {
                        val s = SourceImpl<EffectsBean>(ArrayList(i.childs!!))
                        adapter = s.adapter(AdapterEffectsDetialBinding::class.java){childD,ci,_->
                            childD.root.setOnClickListener {
                                chooseBean?.choose(false)
                                chooseBean = if (ci == chooseBean) {
                                    return@setOnClickListener
                                } else {
                                    ci.choose(true)
                                    ci
                                }
                                d.idChildRecycle.adapter?.notifyDataSetChanged()
                                MusicEffectsViewModel.CHOOSE_EFFECY_KEY.saveStateChange(iAgent,ci)
                            }
                        }
                        d.idChildRecycle.linearLayoutManager {
                            adapter
                        }
                    } else {
                        (adapter as? ListAbstacyAdapter<EffectsBean>)?.apply {
                            (list as? ArrayList)?.let { list ->
                                list.clear()
                                list.addAll(i.childs!!)
                            }
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }


}