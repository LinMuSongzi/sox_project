package com.example.cpp

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.cpp.data.ByteInfo
import com.example.cpp.databinding.AdapterByteShowBinding
import com.musongzi.comment.util.SourceImpl
import com.musongzi.core.ExtensionCoreMethod.adapter
import com.musongzi.core.ExtensionCoreMethod.gridLayoutManager
import com.musongzi.core.base.business.BaseMapBusiness
import com.musongzi.core.base.vm.DataDriveViewModel
import com.musongzi.core.itf.IBusiness
import com.musongzi.core.itf.page.ISource

class ByteReadBusiness: BaseMapBusiness<DataDriveViewModel<*>>() ,ISource<ByteInfo> by SourceImpl(){


    var chooseBean:ByteInfo? = null






}