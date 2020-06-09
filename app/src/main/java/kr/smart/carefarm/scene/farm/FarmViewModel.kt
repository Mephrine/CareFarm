package kr.smart.carefarm.scene.farm

import android.content.Context
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kr.smart.carefarm.R
import kr.smart.carefarm.api.APIClient
import kr.smart.carefarm.application.CFApplication
import kr.smart.carefarm.base.BaseViewModel
import kr.smart.carefarm.model.*
import java.util.*
import kotlin.collections.ArrayList

class FarmViewModel(private val context: Context) : BaseViewModel() {
    var farmList = BehaviorSubject.create<List<FarmData>>()
    // current Noti List
    var notiList = BehaviorSubject.create<List<NotiData>>()

    fun fetchFarmList() {
        progress.onNext(true)

        val userId = CFApplication.prefs.userId
        APIClient().getFarmApi().requestFarmList(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                progress.onNext(false)
                farmList.onNext(response.resultList)
            }, { error ->
                progress.onNext(false)

                Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                Log.e("Error", error.message)
                error.printStackTrace()
            }).apply { disposables.add(this) }
    }

    fun fetchNotiList() {
        progress.onNext(true)

        val userId = CFApplication.prefs.userId
        APIClient().getNotiApi().notiAllList(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                progress.onNext(false)
                notiList.onNext(response.resultList)
            }, { error ->
                progress.onNext(false)

                Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                Log.e("Error", error.message)
                error.printStackTrace()
            }).apply { disposables.add(this) }
    }
}