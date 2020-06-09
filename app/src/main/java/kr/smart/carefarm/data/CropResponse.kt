package kr.smart.carefarm.data

import kr.smart.carefarm.model.CropData


data class CropResponse(var code: String, var message: String, var resultList: List<CropData>)