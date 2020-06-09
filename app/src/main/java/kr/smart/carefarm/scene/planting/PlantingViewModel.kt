package kr.smart.carefarm.scene.planting

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
import kr.smart.carefarm.model.PlantingData
import kr.smart.carefarm.model.NotiData
import kr.smart.carefarm.model.ControlData
import kr.smart.carefarm.utils.L

class PlantingViewModel(private val context: Context, private val farmId: String) : BaseViewModel() {
    // current Noti List
    var notiList = BehaviorSubject.create<List<NotiData>>()
    var plantingList = BehaviorSubject.create<List<PlantingData>>()
    var sbjLogout = BehaviorSubject.createDefault(false)

    var weatherNm = BehaviorSubject.create<String>()

//    fun fetchPlantingList() {
        //test
//        var listPlanting = ArrayList<PlantingData>()
//        var listNoti = ArrayList<NotiData>()
//        var listWindow = ArrayList<WindowData>()
//        for (i in 1..3) {
//            val window = WindowData("window ${i}","측창 ${i}", "${i}0% 열림")
//            listWindow.add(window)
//        }
//
//        var listWindow2 = ArrayList<WindowData>()
//        for (i in 1..5) {
//            val window = WindowData("window ${i}","천창 ${i}", "${i}0% 닫힘")
//            listWindow2.add(window)
//        }
//
//        var listWindow3 = ArrayList<WindowData>()
//        for (i in 1..3) {
//            val window = WindowData("window ${i}","관수 ${i}", "${i}0% 열림")
//            listWindow3.add(window)
//        }
//
//        for (i in 1..10) {
//            var listPlanting = ArrayList<PlantingData>()
//            for (j in 1..10) {
//                if (j == 2 || j == 4 || j == 7) {
//                    val window = WindowData("window ${i}","측창", "${i}0% 열림")
//                    val window2 = WindowData("window ${i}","관수", "${i}0% 열림")
//                    listWindow.clear()
//                    listWindow.add(window)
//                    listWindow2.clear()
//                    listWindow2.add(window2)
//                    val planting =
//                        PlantingData("id", "S", "${j} 동", listWindow, listWindow2, emptyList())
//                    listPlanting.add(planting)
//                } else {
//                    val planting =
//                        PlantingData("id", "P", "${j} 동", listWindow, listWindow2, listWindow3)
//                    listPlanting.add(planting)
//                }
//            }
//            listPlanting.add(PlantingData("221","농장${i}", listPlanting))
//            val notiData = NotiData("notiId${i}","노티 알림입니다. ${i}")
//            listNoti.add(notiData)
//        }
//        plantingList.onNext(listPlanting)
//        notiList.onNext(listNoti)

//        val userId = CFApplication.prefs.userId
//        APIClient().getPlantingApi().requestPlantingList(userId)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ response ->
//                progress.onNext(false)
//                plantingList.onNext(response.listPlanting)
//                notiList.onNext(response.listNoti)
//            }, { error ->
//                progress.onNext(false)
//
//                Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
//                Log.e("Error", error.message)
//                error.printStackTrace()
//            }).apply { disposables.add(this) }
//    }

    fun fetchLogout() {
        progress.onNext(true)
        val userId = CFApplication.prefs.userId
        APIClient().getLogoutApi().sendLogout(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                progress.onNext(false)
                if (response.code == "00") {
                    sbjLogout.onNext(true)
                } else {
                    Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                }
            }, { error ->
                progress.onNext(false)

                Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                Log.e("Error", error.message)
                error.printStackTrace()
            }).apply { disposables.add(this) }
    }

    fun fetchPlantingList() {
        progress.onNext(true)
        APIClient().getPlantingApi().requestFarmDetail(farmId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                progress.onNext(false)
                plantingList.onNext(response.resultList)

                response.fctTimesList?.let {
                    if (it.size > 0) {
                        weatherNm.onNext(it.first().weaText)
                    }
                }
                L.d("test####### 1")
            }, { error ->
                progress.onNext(false)

                Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                Log.e("Error", error.message)
                error.printStackTrace()
            }).apply { disposables.add(this) }
    }

    fun fetchNotiList(farmId: String) {
        progress.onNext(true)

        val userId = CFApplication.prefs.userId
        APIClient().getNotiApi().notiFarmList(userId, farmId)
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