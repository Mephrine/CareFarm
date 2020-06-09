package kr.smart.carefarm.data

import kr.smart.carefarm.model.FarmData
import kr.smart.carefarm.model.NotiData


data class FarmResponse(var code: String,
                        var message: String,
                        var resultList: List<FarmData>)