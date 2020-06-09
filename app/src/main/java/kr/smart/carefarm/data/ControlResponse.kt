package kr.smart.carefarm.data

import kr.smart.carefarm.model.FarmEquipData


data class ControlResponse(var farmequiptagVO: FarmEquipData,
                           var resultCd: Boolean)