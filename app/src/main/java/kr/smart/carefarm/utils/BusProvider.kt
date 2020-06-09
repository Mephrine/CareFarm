package kr.smart.carefarm.utils

import com.squareup.otto.Bus

class BusProvider {
    companion object {
        val instance = Bus()
    }
}