package kr.smart.carefarm.data

import kr.smart.carefarm.model.PlantingData
import kr.smart.carefarm.model.WeatherData

data class PlantingListResponse(var code: String,
                                var message: String,
                                var resultList: List<PlantingData>,
                                var fctTimesList: List<WeatherData>?)