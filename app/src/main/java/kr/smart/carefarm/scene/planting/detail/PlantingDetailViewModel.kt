package kr.smart.carefarm.scene.planting.detail

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kr.smart.carefarm.R
import kr.smart.carefarm.api.APIClient
import kr.smart.carefarm.application.CFApplication
import kr.smart.carefarm.base.BaseViewModel
import kr.smart.carefarm.model.ControlData
import kr.smart.carefarm.model.CropData
import kr.smart.carefarm.model.PlantingData
import kr.smart.carefarm.utils.ImageUtils
import kr.smart.carefarm.utils.L
import kr.smart.carefarm.utils.toRequestBody
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


enum class SnsrType {
    TEMPER, HUMID, CO2
}

enum class StopType {
    WINDOW, WATER
}


data class ControlType (
    val tagDtlDivn: String
) {
    enum class Type { SIDE, TOP, WATER }
    var type =  Type.SIDE
    var typeNmResource: Int = R.string.planting_side_window

    init {
        when(tagDtlDivn) {
            "KH1601" -> {
                type = Type.SIDE
                typeNmResource = R.string.planting_side_window
            }
            "KH1602" -> {
                type = Type.TOP
                typeNmResource = R.string.planting_top_window
            }
            "KH1607" -> {
                type = Type.WATER
                typeNmResource = R.string.planting_watering
            }
//            "M" -> type = Type.MIX
        }

    }
}

data class StopControl(var farmequiptagId: String,
                        var time: Long,
                       var addTime: Long,
                       var retain: String,
                       var type: StopType,
                       var isOpen: Boolean)

class PlantingDetailViewModel(private val context: Context): BaseViewModel(){
    enum class FileType {
        NONE, CAMERA, PHOTO, VIDEO
    }

    enum class IS_BETWEEN {
        LOWER, BETWEEN, UPPER
    }

    val sbjControlList = BehaviorSubject.create<List<ControlData>>()
    val sbjcropList = BehaviorSubject.create<List<CropData>>()

    // on -> 퍼센트 선택 0% 노출 후 피커 띄우기. 닫기는 0%로 변경.
//    val sbjWindowPercent = BehaviorSubject.create<String>()
//    val sbjWindowState = BehaviorSubject.create<Boolean>()

    // 관수
//    val sbjWaterTime = BehaviorSubject.create<String>()
//    val sbjWaterState = BehaviorSubject.create<Boolean>()
//    val sbjWaterTimeStart = BehaviorSubject.createDefault("00:00")
//    val sbjWaterTimeDuration = BehaviorSubject.createDefault("00:00")


    // 온도 임계치
    val sbjTempCriticalMax = BehaviorSubject.create<String>()
    val sbjTempCriticalMin = BehaviorSubject.create<String>()

    //maxWrnVal
    // 온도
    val sbjTemp = BehaviorSubject.create<String>()

    // 습도 임계치
    val sbjHumidityCriticalMax = BehaviorSubject.create<String>()
    val sbjHumidityCriticalMin = BehaviorSubject.create<String>()
    // 습도
    val sbjHumidity = BehaviorSubject.create<String>()

    // 이산화탄소 임계치
    val sbjCo2CriticalMax = BehaviorSubject.create<String>()
    val sbjCo2CriticalMin = BehaviorSubject.create<String>()
    // 이산화탄소
    val sbjCo2 = BehaviorSubject.create<String>()

    // 선택된 센서 타입
    val sbjSnsrType = BehaviorSubject.createDefault<SnsrType>(SnsrType.TEMPER)

    val sbjShowAlarmSensor = BehaviorSubject.createDefault<Boolean>(false)

//    val sbjPlantingDetail = BehaviorSubject.create<PlantingData>()

    var farmcctvId: String = ""
//    var farmequiptagWindowId: String = ""
//    var farmequiptagWaterId: String = ""

    var tempCriId = ""
    var humidityCriId = ""
    var co2CriId = ""

    var growfacId = ""
    var farmId = ""

    var beforeMinVal = "0"
    var beforeMaxVal = "0"

    var isChanged = false

    // timer

    var timerTotTime = 0
    var sbjStopControl = BehaviorSubject.create<StopControl>()
    var listStopControl = ArrayList<StopControl>()
    var timer: Timer? = null


    var isOpenOptionEquipId = ArrayList<String>()


    // 0 -> 멈춤 / 1-> 열림 / 2-> 닫힘
//    var currentWindowState = "0"
    // 0 -> 멈춤 / 1-> 작동
//    var currentWaterState = "0"

    var selectedUri = BehaviorSubject.create<Uri?>()
    var fileType: FileType = FileType.NONE

    fun selectedImageType() {
        fileType = FileType.PHOTO
    }

    fun selectedVideoType() {
        fileType = FileType.PHOTO
    }

    fun selectedNoneType() {
        fileType = FileType.NONE
    }

    fun selectedCameraType() {
        fileType = FileType.CAMERA
    }

    init {
        sbjStopControl
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it.type) {
                    StopType.WINDOW -> {
                        fetchUpdateControl(it.farmequiptagId, it.time.toString())
                    }
                    StopType.WATER -> {
                        fetchWaterStopControl(it.farmequiptagId)
                    }
                }
            }.apply { disposables.add(this) }
    }

    override fun onCleared() {
        stopTimer()
        super.onCleared()
    }

    fun fetchCCTVList() {
        // CCTV리스트
        APIClient().getCCTVApi().growthCCTVList(growfacId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                L.d("response : ${response}")
                if (response.resultList.isNotEmpty()){
                    farmcctvId = response.resultList.first().farmcctvId
                }
            }, { error ->

                Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                Log.e("Error", error.message)
                error.printStackTrace()
            }).apply { disposables.add(this) }
    }

    fun fetchSnsrCriticalList() {
//        progress.onNext(true)
        this.fetchCCTVList()

        APIClient().getPlantingDetailApi().requestSensorCriticalList(growfacId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                L.d("getPlantingDetailApi : ${response}")
//                progress.onNext(false)
                var showBtn = false

                response.temperList?.let {
                    L.d("temperList### : ${it}")
                    if (it.isNotEmpty()) {
                        L.d("temperList### 2")
                        showBtn = true
                        L.d("it.first().maxWrnVal : ${it.first().maxWrnVal}")
                        sbjTempCriticalMax.onNext(it.first().maxWrnVal)
                        sbjTempCriticalMin.onNext(it.first().minWrnVal)
                        tempCriId = it.first().criId
                    }
                }
                response.humidList?.let {
                    L.d("humidList### : ${it}")
                    if (it.isNotEmpty()) {
                        showBtn = true
                        sbjHumidityCriticalMax.onNext(it.first().maxWrnVal)
                        sbjHumidityCriticalMin.onNext(it.first().minWrnVal)
                        humidityCriId = it.first().criId
                    }
                }

                response.carDioList?.let {
                    if (it.isNotEmpty()) {
                        showBtn = true
                        sbjCo2CriticalMax.onNext(it.first().maxWrnVal)
                        sbjCo2CriticalMin.onNext(it.first().minWrnVal)
                        co2CriId = it.first().criId
                    }
                }
                sbjShowAlarmSensor.onNext(showBtn)

            }, { error ->
//                progress.onNext(false)

                Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                Log.e("Error", error.message)
                error.printStackTrace()
            }).apply { disposables.add(this) }
    }

    fun fetchModifySnsr() {
        progress.onNext(true)
        var criId: String = ""
        var maxWrnVal: String = ""
        var minWrnVal: String = ""
        val loginId = CFApplication.prefs.userId

        when(sbjSnsrType.value) {
            SnsrType.TEMPER -> {
                criId = tempCriId
                maxWrnVal = sbjTempCriticalMax.value ?: ""
                minWrnVal = sbjTempCriticalMin.value ?: ""
            }
            SnsrType.HUMID -> {
                criId = humidityCriId
                maxWrnVal = sbjHumidityCriticalMax.value ?: ""
                minWrnVal = sbjHumidityCriticalMin.value ?: ""
            }
            SnsrType.CO2 -> {
                criId = co2CriId
                maxWrnVal = sbjCo2CriticalMax.value ?: ""
                minWrnVal = sbjCo2CriticalMin.value ?: ""
            }
        }

        APIClient().getPlantingDetailApi().requestModSensorCritical(criId, maxWrnVal, minWrnVal, loginId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                progress.onNext(false)
                if (response.code == "00") {
                    Toast.makeText(context, context.getString(R.string.control_sensor_warn_change_cmpl), Toast.LENGTH_SHORT).show()
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

    fun fetchWindowControl(statusCd: String, farmequiptagWindowId: String, retain: String) {
        progress.onNext(true)



        L.d("control fetchWindowControl!!!!!! response : ${statusCd} | ${retain}")
        // retain이 0인 상태에서 닫힘 버튼 누를 경우, retain이 180인 상태에서 열림 버튼을 누른 경우.
        if ((retain == "0" && statusCd == "2") || (retain == "180" && statusCd == "1")) {
            fetchCloseWindowControl(farmequiptagWindowId)
            return
        }

        var retainConvert = retain.toIntOrNull()
        var curProgress = retain
        var isOpen = false

        if (statusCd == "1") {
            retainConvert = 180-(retain.toInt())
            isOpen = true
        }

        retainConvert?.let {
            if (it > 0) {
                curProgress = ((it / 180) * 100).toString()
            }
        }

        curProgress = context.getString(R.string.control_start, curProgress) + "%"

        if(farmequiptagWindowId.isNotEmpty()) {
            APIClient().getControlApi().requestControl(farmequiptagWindowId, statusCd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    progress.onNext(false)
                    if (response.resultCd) {
                        progress.onNext(false)
                        Toast.makeText(
                            context,
                            curProgress,
                            Toast.LENGTH_SHORT
                        ).show()

//                        fetchUpdateControl(farmequiptagWindowId, retain)

                        addStopListItem(farmequiptagWindowId, StopType.WINDOW, retainConvert?.toLong() ?: 0 ,retain, isOpen)

                    } else {
                        Toast.makeText(context, context.getString(R.string.control_fail), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    progress.onNext(false)

                    Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                    Log.e("Error", error.message)
                    error.printStackTrace()
                }).apply { disposables.add(this) }
        }
    }

    fun fetchUpdateControl(farmequiptagWindowId: String, retain: String) {
        val duration = retain.toDouble()
        L.d("control fetchUpdateControl!!!!!! response")
        APIClient().getControlApi().getUpdateState(farmequiptagWindowId, retain)
//            .delay(duration.toLong(), TimeUnit.SECONDS, Schedulers.io())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                L.d("control update!!! response : ${response}")
                fetchCloseWindowControl(farmequiptagWindowId)
            }, { error ->
                fetchCloseWindowControl(farmequiptagWindowId)
            }).apply { disposables.add(this) }
    }

    fun fetchCloseWindowControl(farmequiptagWindowId: String) {
//        progress.onNext(true)
        L.d("control close!!!!!! response")
        if(farmequiptagWindowId.isNotEmpty()) {
            APIClient().getControlApi().requestControl(farmequiptagWindowId, "0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
//                    progress.onNext(false)
                    if (response.resultCd) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.control_success),
                            Toast.LENGTH_SHORT
                        ).show()



                        isChanged = true
                        val item = response.farmequiptagVO
//                        val list = sbjControlList.value
//                        val newList = ArrayList<ControlData>()
//                        for (i in list ?: emptyList()) {
//                            if (i.farmequiptagId.equals(farmequiptagWindowId)) {
//                                i.retVal = item.retVal
//                                i.retStatus = item.retStatus
//                                newList.add(i)
//                            } else {
//                                newList.add(i)
//                            }
//                        }

                        removeStopListItem(farmequiptagWindowId, item.retVal, item.retStatus)

                    } else {
                        Toast.makeText(context, context.getString(R.string.control_fail), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    progress.onNext(false)

                    Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                    Log.e("Error", error.message)
                    error.printStackTrace()
                }).apply { disposables.add(this) }
        }
    }



    fun fetchWaterControl(farmequiptagWaterId: String, timeDuration: String) {
        progress.onNext(true)
        // 멈춤 -> 0 / 열기 -> 1
        var statusCd = "1"

//        val timeArray = (timeDuration ?: "00:00").split(":")
//
//        val minute = timeArray.first()
//        val second = timeArray.last()
//
//        val duration = (minute.toInt()*60 + second.toInt()).toLong()
//
//        L.d("time : ${minute} | ${second} | ${duration}")

//        L.d("request url : ${farmequiptagWaterId} | ${statusCd}")
        if (farmequiptagWaterId.isNotEmpty()) {
            APIClient().getControlApi().requestControl(farmequiptagWaterId, statusCd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    L.d("control start!!! response : ${response}")
                    progress.onNext(false)
                    if (response.resultCd) {
                        addStopListItem(farmequiptagWaterId, StopType.WATER, timeDuration.toLong(), timeDuration, true)
//                        Toast.makeText(
//                            context,
//                            context.getString(R.string.control_success),
//                            Toast.LENGTH_SHORT
//                        ).show()

                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.control_fail),
                            Toast.LENGTH_SHORT
                        ).show()
                        changeControlState(farmequiptagWaterId, false)
                    }
                }, { error ->
                    progress.onNext(false)

                    Toast.makeText(
                        context,
                        context.getString(R.string.fail_network),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("Error", error.message)
                    error.printStackTrace()
                }).apply { disposables.add(this) }
        }
    }


    fun fetchWaterStopControl(equiId: String) {
        L.d("control stop!!! : ${equiId}")

        APIClient().getControlApi().requestControl(equiId, "0")
//            .delay(duration.toLong(), TimeUnit.SECONDS, Schedulers.io())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                L.d("control stop!!! response : ${response}")
                if (response.resultCd) {
                    progress.onNext(false)
                    Toast.makeText(
                        context,
                        context.getString(R.string.control_success),
                        Toast.LENGTH_SHORT
                    ).show()



                    val item = response.farmequiptagVO
//                    val list = sbjControlList.value
//                    val newList = ArrayList<ControlData>()
//                    for (i in list ?: emptyList()) {
//                        if (i.farmequiptagId.equals(equiId)) {
//                            i.retVal = item.retVal
//                            i.retStatus = item.retStatus
//                            newList.add(i)
//                        } else {
//                            newList.add(i)
//                        }
//                    }

                    removeStopListItem(equiId, item.retVal, item.retStatus)

                    isChanged = true


//                    fetchReloadControl()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.control_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, { error ->
                progress.onNext(false)

                Toast.makeText(
                    context,
                    context.getString(R.string.fail_network),
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Error", error.message)
                error.printStackTrace()
            }).apply { disposables.add(this) }
    }

    fun chkControlChangeState(equiId: String){

        APIClient().getControlApi().chkControlChangeState(equiId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.resultCd) {
                    L.d("chkControlChangeState : ${response}")
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

    fun getControlState(equiId: String){

        APIClient().getControlApi().getControlState(equiId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.resultCd) {
                    L.d("getControlState : ${response}")
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

    fun fetchReloadControl(){

        progress.onNext(true)
        APIClient().getPlantingDetailApi().requestFarmDetail(farmId, growfacId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                progress.onNext(false)
                if (response.code == "00") {
                    L.d("getControlState : ${response}")
                    if (response.resultList.size > 0) {
                        reloadControlList(response.resultList.first())
                    }
                } else {
                    var message = response.message
                    if (message.isEmpty()) {
                        message = context.getString(R.string.fail_network)
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }, { error ->
                progress.onNext(false)

                Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                Log.e("Error", error.message)
                error.printStackTrace()
            }).apply { disposables.add(this) }
    }

    fun fetchCropList() {
        //growfacId
        APIClient().getCropApi().requestCropList(farmId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
//                progress.onNext(false)
                if (response.code == "00") {
                    L.d("getControlState : ${response}")
                    sbjcropList.onNext(response.resultList)
                } else {
                    var message = response.message
                    if (message.isEmpty()) {
                        message = context.getString(R.string.fail_network)
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }, { error ->
//                progress.onNext(false)

                Toast.makeText(context, context.getString(R.string.fail_network), Toast.LENGTH_SHORT).show()
                Log.e("Error", error.message)
                error.printStackTrace()
            }).apply { disposables.add(this) }
    }

    fun fetchSendImage() {
        progress.onNext(true)

        val growfacIdBody = toRequestBody(growfacId)

        var imageUpload: MultipartBody.Part? = null
        selectedUri.value?.path?.let {
            //            val file = File(it)
            try {
                val file = ImageUtils.bitmapToFile(context, it)

                when (fileType) {
                    FileType.PHOTO, FileType.CAMERA -> {
                        //multipart/form-data
                        val imageBody = RequestBody.create(MediaType.parse("image/jpg"), file)
                        imageUpload = MultipartBody.Part.createFormData("file_1", file.name, imageBody)
                    }
                    else -> {
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, context.getString(R.string.fail_get_restart), Toast.LENGTH_SHORT).show()
                return
            }
        }

        growfacIdBody?.let {
            APIClient().getFileApi().sendImage(imageUpload, it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { res ->
                        progress.onNext(false)
                        //서버 응답후 처리.
                        if (res.code == "00") {
                            Toast.makeText(context, context.getString(R.string.control_upload_image), Toast.LENGTH_SHORT).show()
                        } else {
                            var message = res.message
                            if (message.isEmpty()) {
                                message = context.getString(R.string.fail_network)
                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    { e ->
                        progress.onNext(false)
                        Toast.makeText(context, context.getString(R.string.fail_network_restart), Toast.LENGTH_SHORT).show()
                    }
                ).apply { disposables.add(this) }
        }


    }


    fun isBetween(minNumStr: String, chkNumStr: String,maxNumStr: String): IS_BETWEEN {
        val min = minNumStr.toFloat()
        val max = maxNumStr.toFloat()
        val num = chkNumStr.toFloat()
        // max보다 더 큼
        if (num > max) {
            return IS_BETWEEN.UPPER
        }
        // min보다 작음.
        else if (min > num) {
            return IS_BETWEEN.LOWER
        } else {
            return IS_BETWEEN.BETWEEN
        }
    }

    fun reloadControlList(data: PlantingData) {
        val controlList = ArrayList<ControlData>()

        // 제어 리스트들만 묶기.
        val sideWindowList = data.windowSTagList
        val topWindowList = data.windowUTagList
        val waterList = data.warterTagList

        sideWindowList?.let {
            controlList.addAll(it)
        }

        topWindowList?.let {
            controlList.addAll(it)
        }

        waterList?.let {
            controlList.addAll(it)
        }


        sbjControlList.onNext(controlList)

    }

    // 닫기 타이머 계산
    private fun startTimer() {
        if (timer == null) {
            timer = Timer()
        }

        val timerTask = object : TimerTask() {
            override fun run() {
                if (listStopControl.size < 1) {
                    stopTimer()
                    return
                }

                for (item in listStopControl) {
                    timeDiff(item)
                }
            }
        }
        timer!!.schedule(timerTask, 0, 1000)
    }

    fun stopTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    private fun timeDiff(timeData: StopControl, forcedStop: Boolean = false) {
        val curTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        var diffTime = (timeData.time - curTime)

        L.d("control timeDiff!!!!!! : ${diffTime} | ${timeData.time}  ||  ${curTime} ")

        if(diffTime <= 0 || forcedStop) {
            // 최대값 - diff 값
            if (!timeData.isOpen) {
                diffTime = -(timeData.retain.toLongOrNull() ?:0 - diffTime)
            } else {
                diffTime = 180 - diffTime
            }
            timeData.time = (timeData.retain.toLongOrNull() ?: 0) + diffTime
            L.d("control timeDiff!!!!!! : ${timeData.time} | ${diffTime}")
            sbjStopControl.onNext(timeData)

            if (listStopControl.size == 1) {
                stopTimer()
            }
        }
    }

    private fun addStopListItem(farmequiptagId: String, type: StopType, addTime: Long, retain: String, isOpen: Boolean) {
        L.d("control addStopListItem!!!!!! init : ${addTime} ")


        val curTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + addTime
        listStopControl.add(StopControl(farmequiptagId, curTime, addTime, retain, type, isOpen))
        changeControlState(farmequiptagId, true)

        L.d("control addStopListItem!!!!!! set : ${listStopControl} ")

        if (listStopControl.size == 1) {
            startTimer()
        }
    }

    private fun removeStopListItem(farmequiptagId: String, retVal: String?, retStatus: String?) {
        if (listStopControl.size > 0) {
            for (i in 0..listStopControl.size) {
                if (listStopControl[i].farmequiptagId == farmequiptagId) {
                    listStopControl.removeAt(i)
                    changeControlState(farmequiptagId, false, retVal, retStatus)

                    if (listStopControl.size < 1) {
                        stopTimer()
                    }
                    L.d("control removeStopListItem!!!!!! remove : ${i}")
                    return
                }
            }
        } else {
            changeControlState(farmequiptagId, false, retVal, retStatus)
        }
    }

    private fun changeControlState(farmequiptagId: String, isControlling: Boolean, retVal: String? = null, retStatus: String? = null) {
        val list = sbjControlList.value ?: emptyList()
        for (item in list) {
            if (item.farmequiptagId == farmequiptagId) {
                item.isControlling = isControlling
                retVal?.let {
                    item.retVal = it
                }
                retStatus?.let {
                    item.retStatus = it
                }
                L.d("item : ${item}")
            }
        }
        sbjControlList.onNext(list)
    }

    fun stopControl(farmequiptagId: String){
        for (item in listStopControl) {
            if (item.farmequiptagId == farmequiptagId) {
                timeDiff(item, true)
            }
            break
        }
    }

    fun validation(val1: String, val2: String): Boolean {
        if (val1.isNotEmpty() && val2.isNotEmpty()) {
            return true
        }

        return false
    }
}
