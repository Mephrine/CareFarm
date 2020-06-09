package kr.smart.carefarm.config

import android.view.SurfaceView

class C {
    companion object{
        const val BASE_URL = "http://49.50.167.56:8080/"
        const val REQ_CODE_NFC_CHECK = 100
        const val REQ_CODE_NFC_DELIVERY = 101
        const val REQ_CODE_NFC_WRITE = 102


        const val INTENT_TITLE = "title"
        const val INTENT_FARM_ID = "farmId"
        const val INTENT_CEO_NM = "ceoNm"
        const val INTENT_WEATHER = "weatherNm"
        const val INTENT_NOTI_LIST = "notiList"
        const val INTENT_PLANTING_DETAIL = "plantingDetail"
        const val INTENT_RELOAD = "reload"

        const val INTENT_LIST = "list"
        const val INTENT_IS_INIT = "init"
        const val INTENT_URL_STRING = "url"
        const val INTENT_URL_CCTV_FARM_ID= "cctvFarmId"

        // MoviePlayView
        var mPreview: SurfaceView? = null
        var ip: String = "172.31.77.160"
        var id: String = "admin"
        var pw: String = "9999"
        var channel: String = "1"
        var webport: String = "80"
        var videoport: String = "9001"
        var Stun_ip: String = ""
        var Stun_port: String = ""
        var disk_id: String = ""
        var disk_key: String = ""
        var disk_num = 0
        var member: String = ""
        var codec: String = "H264"
        var iframe_flag = 0
        var lowplay_flag = 0
    }
}