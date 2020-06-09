package kr.smart.carefarm.application

import androidx.multidex.MultiDexApplication
import com.squareup.leakcanary.LeakCanary
import kr.smart.carefarm.utils.CFSharedPreferences

class CFApplication : MultiDexApplication() {


    init {
        INSTANCE = this
//        System.loadLibrary("basicplayer")
    }


    companion object {
        lateinit var INSTANCE: CFApplication
        lateinit var prefs : CFSharedPreferences
        val DEBUG = true
    }

    override fun onCreate() {
        super.onCreate()
        prefs = CFSharedPreferences(applicationContext)
        setLeakCanary()
    }

    private fun setLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) { // This process is dedicated to LeakCanary for heap analysis.
// You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }

}