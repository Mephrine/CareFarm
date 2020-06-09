package kr.smart.carefarm.data

import kr.smart.carefarm.model.PlantingData
import kr.smart.carefarm.model.SnsrCriticalData


data class SnsrCriticalListResponse(var temperList: List<SnsrCriticalData>?,
                                    var humidList: List<SnsrCriticalData>?,
                                    var carDioList: List<SnsrCriticalData>?)