package com.example.textui

import android.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_LAZY
import androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_NONE
import com.example.textui.databinding.ItemBigBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.musongzi.comment.util.SourceImpl
import com.musongzi.core.ExtensionCoreMethod.adapter
import com.musongzi.core.base.business.BaseMapBusiness
import com.musongzi.core.itf.IViewInstance
import com.musongzi.core.itf.page.ISource
import com.musongzi.core.util.ScreenUtil

class RectBusiness : BaseMapBusiness<IViewInstance>() {



    fun initRecycleView(recyclerView: RecyclerView){

//        GridLayoutManager(null,3,GridLayoutManager.VERTICAL,false).spanSizeLookup =

        recyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
//        recyclerView.layoutManager = FlexboxLayoutManager(recyclerView.context)
        recyclerView.adapter = nativeData().adapter(ItemBigBinding::class.java){
            d,i,p->
            when(i.type){
                1->{

                    d.root.layoutParams.width = (ScreenUtil.getScreenWidth() - ScreenUtil.dp2px(20f)) / 2
                    d.root.layoutParams.height = (ScreenUtil.getScreenWidth() - ScreenUtil.dp2px(20f)) / 4
                }
                2->{
//                    (d.root.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.apply{
//                        this.isFullSpan = true
//                    }
                    d.root.layoutParams.width = (ScreenUtil.getScreenWidth() - ScreenUtil.dp2px(20f)) / 2
                    d.root.layoutParams.height = (ScreenUtil.getScreenWidth() - ScreenUtil.dp2px(20f)) / 2
                }
                3->{
                    d.root.layoutParams.width = (ScreenUtil.getScreenWidth() - ScreenUtil.dp2px(20f)) / 3
                    d.root.layoutParams.height = (ScreenUtil.getScreenWidth() - ScreenUtil.dp2px(20f)) / 4
                }
            }

            d.idPosition.text = p.toString()
        }
    }

    fun nativeData():ISource<RectBean>{
        return SourceImpl<RectBean>().apply {
            datas.add(RectBean(1, Color.parseColor("#b1cd0b")))
            datas.add(RectBean(1, Color.parseColor("#9af40b")))
            datas.add(RectBean(2, Color.parseColor("#0bcdf4")))
            datas.add(RectBean(1, Color.parseColor("#f81f0b")))
            datas.add(RectBean(1, Color.parseColor("#0acdfb")))
            datas.add(RectBean(3, Color.parseColor("#ff780b")))
            datas.add(RectBean(3, Color.parseColor("#0a785b")))
            datas.add(RectBean(3, Color.parseColor("#78cd0b")))
            datas.add(RectBean(1, Color.parseColor("#9acd0b")))
            datas.add(RectBean(1, Color.parseColor("#fe456b")))
            datas.add(RectBean(1, Color.parseColor("#9a134a")))
            datas.add(RectBean(1, Color.parseColor("#098d0b")))
            datas.add(RectBean(1, Color.parseColor("#45bd0b")))
            datas.add(RectBean(1, Color.parseColor("#444a0b")))
            datas.add(RectBean(2, Color.parseColor("#fa09f4")))
            datas.add(RectBean(1, Color.parseColor("#61bf0b")))
            datas.add(RectBean(1, Color.parseColor("#ddcdfb")))
        }
    }



}