package kr.smart.carefarm.data

import kr.smart.carefarm.model.CropDetailData

data class CropDetailResponse(var code: String, var message: String, var cropcylList: List<CropDetailData>)