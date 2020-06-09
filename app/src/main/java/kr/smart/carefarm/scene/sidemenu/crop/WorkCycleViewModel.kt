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
import kr.smart.carefarm.model.CropDetailData
import kr.smart.carefarm.utils.L

class WorkCycleViewModel(private val context: Context): BaseViewModel(){
    val sbjRegCrop = BehaviorSubject.create<List<CropDetailData>>()

    var cropId: String = ""

    fun fetchCropDetailList() {
        progress.onNext(true)

//        val userId = CFApplication.prefs.userId
        APIClient().getCropApi().requestCropDetail(cropId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                progress.onNext(false)
                sbjRegCrop.onNext(response.cropcylList)
            }, { error ->
                progress.onNext(false)

                Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                Log.e("Error", error.message)
                error.printStackTrace()
            }).apply { disposables.add(this) }
    }

    fun saveWorkCycleList(workNm: String, workDt: String, workCont: String) {
        if (workNm.isEmpty() || workDt.isEmpty() || workCont.isEmpty()) {
            return
        }

        progress.onNext(true)

        val ordSeq = (sbjRegCrop.value?.size ?: 0).toString()
        val cylNm = workNm
        val cylTerm = workDt
        val useYn = "Y"
        val noteTxt = workCont
        val loginId = CFApplication.prefs.userId


//        val userId = CFApplication.prefs.userId
        APIClient().getCropApi().requestInsertSetCropcyl(cropId, ordSeq, cylNm, cylTerm, useYn, noteTxt, loginId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                //reload
                fetchCropDetailList()

            }, { error ->
                progress.onNext(false)

                Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                Log.e("Error", error.message)
                error.printStackTrace()
            }).apply { disposables.add(this) }
    }
}