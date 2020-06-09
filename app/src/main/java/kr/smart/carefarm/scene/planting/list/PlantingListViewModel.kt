package kr.smart.carefarm.scene.planting.list

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
import kr.smart.carefarm.model.FarmData
import kr.smart.carefarm.model.PlantingData


class PlantingListViewModel(private val context: Context) : BaseViewModel() {
    var moveDetail = BehaviorSubject.create<PlantingData>()
    var plantingList = BehaviorSubject.create<List<PlantingData>>()

    var farmId = ""

}