package kr.smart.carefarm.scene.farm

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kr.smart.carefarm.model.FarmData


class FarmEventsBus {
    private fun FarmEventsBus() {}
//    private val fragmentEventSubject = PublishSubject.create<Int>()
//    val fragmentEventObservable: Observable<Int>
//        get() = fragmentEventSubject
//
//    fun postFragmentAction(actionId: Int) {
//        fragmentEventSubject.onNext(actionId)
//    }

    private val activityListSubject = PublishSubject.create<List<FarmData>>()
    val activityFarmListObservable: Observable<List<FarmData>>
        get() = activityListSubject

    fun postFarmListAction(list: List<FarmData>) {
        activityListSubject.onNext(list)
    }

    private val activityTitleSubject= PublishSubject.create<String>()
    val activityTitleObservable: Observable<String>
        get() = activityTitleSubject

    fun postActivityTitleAction(title: String) {
        activityTitleSubject.onNext(title)
    }

    companion object {
        private var mInstance: FarmEventsBus? = null
        val instance: FarmEventsBus
            get() {
                if (mInstance == null) {
                    mInstance = FarmEventsBus()
                }
                return mInstance!!
            }
    }
}