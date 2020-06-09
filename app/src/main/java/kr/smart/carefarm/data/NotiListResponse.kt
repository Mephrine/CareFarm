package kr.smart.carefarm.data

import kr.smart.carefarm.model.NotiData

data class NotiListResponse(var code: String,
                                var message: String,
                                var resultList: List<NotiData>,
                                var totCnt: String)