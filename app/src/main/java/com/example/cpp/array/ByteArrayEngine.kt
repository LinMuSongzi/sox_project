package com.example.cpp.array

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cpp.business.SoudSoxBusiness.Companion.pathConvetInputStream
import com.example.cpp.data.ByteInfo
import com.example.cpp.databinding.AdapterByteShowBinding
import com.musongzi.core.ExtensionCoreMethod.adapter
import com.musongzi.core.annotation.CollecttionsEngine
import com.musongzi.core.base.business.collection.BaseMoreViewEngine
import com.musongzi.core.itf.page.IPageEngine
import com.musongzi.core.util.ScreenUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import java.io.InputStream

@CollecttionsEngine(isEnableReFresh = true, isEnableLoadMore = true, isEnableEventBus = true)
class ByteArrayEngine : BaseMoreViewEngine<Byte, List<Byte>>(),
    ObservableOnSubscribe<List<Byte>> {

    companion object {
        const val PATH_KEY = "path"
    }

    var chooseBean: ByteInfo? = null
    var thisPage = 0;
    var inputStream: InputStream? = null
    var byteArray = ByteArray(pageSize())
    lateinit var path: String

//    private lateinit var observable :Observable<List<ByteInfo>>

    override fun runOnHadBundleData(it: Bundle) {
//        inputStream = it.getString(PATH_KEY)?.apply {
        path = it.getString(PATH_KEY)!!
        Log.i(TAG, "runOnHadBundleData: $path")
//        }?.pathConvetInputStream()
    }

    override fun getRemoteDataReal(page: Int): Observable<List<Byte>> {
        this.thisPage = page
        return Observable.create(this)
    }

    val maxHeight = ScreenUtil.getScreenWidth() / 8
    override fun myAdapter() = adapter(AdapterByteShowBinding::class.java, { d, t ->
        d.root.layoutParams.height = maxHeight
    }) { d, i, p ->

        ;//"0x"+Integer.toHexString(i.byte)

        if (p > 43) {
            d.idText.text = Html.fromHtml("<font color=#00ff00>$i</font><br/><font color=#cccccc>$p</font>")
        } else {
            d.idText.text = Html.fromHtml("<font color=#ff0000>$i</font><br/><font color=#cccccc>$p</font>")
        }

    }

    override fun thisStartPage() = 1

    override fun pageSize(): Int {
        return 160
    }

    override fun getLayoutManger(): RecyclerView.LayoutManager? {
        return GridLayoutManager(null, 8, LinearLayoutManager.VERTICAL, false)
    }

    override fun transformDataToList(entity: List<Byte>) = entity

    override fun subscribe(emitter: ObservableEmitter<List<Byte>>) {

        if (state() == IPageEngine.STATE_START_REFRASH) {
            if (inputStream != null) {
                inputStream!!.close()
            }
            inputStream = path.pathConvetInputStream()
        }
        inputStream?.apply {
            read(byteArray)

            val datas = arrayListOf<Byte>()
            for (byte in byteArray) {
                datas.add(byte)
            }
            emitter.onNext(datas)
        }
    }
}