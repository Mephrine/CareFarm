package kr.smart.carefarm.scene.farm

import io.reactivex.subjects.BehaviorSubject
import kr.smart.carefarm.base.BaseViewModel
import kr.smart.carefarm.model.FarmData
import kr.smart.carefarm.model.NotiData

class FarmFragmentViewModel: BaseViewModel(){
    var moveSubAct = BehaviorSubject.create<FarmData>()
    var farmList = BehaviorSubject.create<List<FarmData>>()
    var notiList = BehaviorSubject.create<List<NotiData>>()

}