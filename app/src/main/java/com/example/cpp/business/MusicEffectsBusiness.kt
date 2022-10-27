package com.example.cpp.business

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.cpp.R
import com.example.cpp.data.EffectsBean
import com.example.cpp.databinding.AdapterEffectsBinding
import com.example.cpp.vm.MusicEffectsViewModel
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
import com.musongzi.core.itf.IAgent
import com.musongzi.core.itf.page.ISource

class MusicEffectsBusiness : BaseMapBusiness<MusicEffectsViewModel>(), ISource<EffectsBean> by SourceImpl() {

    var openBean: EffectsBean? = null
    var chooseBean: EffectsBean? = null

    override fun afterHandlerBusiness() {
        super.afterHandlerBusiness()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun buildRecycleMusicEffectsData(recyclerView: RecyclerView) {
        recyclerView.linearLayoutManager {
            adapter(AdapterEffectsBinding::class.java) { d, i, p ->
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
                        adapter =
                            TypeSupportAdaper.build(ZERO,
                                i.childs!!, AdapterFileBinding::class.java,{childD,_->
                                    childD.idTitleImage.showImage(com.musongzi.core.R.mipmap.ic_launcher_round)
                                },
                                { childD, childI, _ ->
                                    childD.root.setOnClickListener {
                                        chooseBean?.choose(false)
                                        chooseBean = if (i == chooseBean) {
                                            null;
                                        } else {
                                            i.choose(true)
                                            i
                                        }
                                    }
                                    setText(childD.idTitle, childI.cName)
                                    viewVisibility(childD.idTitleImage,i.isChoose())
                                }, false)
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