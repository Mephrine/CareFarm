package kr.smart.carefarm.scene.sidemenu.crop

import android.content.Context
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kr.smart.carefarm.R
import kr.smart.carefarm.api.APIClient
import kr.smart.carefarm.application.CFApplication
import kr.smart.carefarm.base.BaseViewModel
import kr.smart.carefarm.model.*


class RegCropViewModel(private val context: Context): BaseViewModel(){

    val sbjRegCrop = BehaviorSubject.create<List<CropData>>()
    val sbjMoveWorkCycle = BehaviorSubject.create<CropData>()


    fun fetchRegCropList(farmId: String) {
        progress.onNext(true)

//        val userId = CFApplication.prefs.userId
        APIClient().getCropApi().requestCropList(farmId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                progress.onNext(false)
                sbjRegCrop.onNext(response.resultList)
            }, { error ->
                progress.onNext(false)

                Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                Log.e("Error", error.message)
                error.printStackTrace()
            }).apply { disposables.add(this) }
    }
}