package kr.smart.carefarm.lib

import android.app.Activity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import kr.smart.carefarm.R
import kr.smart.carefarm.config.C

class Tab2 : Activity(), SurfaceHolder.Callback {
    var mPreview: SurfaceView? = null
    var mHolder: SurfaceHolder? = null
    private var mMediaPlayView: MoviePlayView? = null
    var mode = NONE
    var posX1 = 0
    var posX2 = 0
    var posY1 = 0
    var posY2 = 0
    var oldDist = 1f
    var newDist = 1f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cctv)
//        C.mPreview = findViewById<View>(R.id.surfaceView1) as SurfaceView
//        mPreview = C.mPreview
//        mHolder = mPreview!!.holder
//        mHolder!!.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
                                height: Int) { // TODO Auto-generated method stub
        println("width => $width")
        println("height => $height")
    }

    override fun surfaceCreated(holder: SurfaceHolder) { // TODO Auto-generated method stub
        mHolder!!.setFixedSize(720, 405)
        println("MoviePlayView before")
        mMediaPlayView = MoviePlayView()
        println("MoviePlayView after")
        mMediaPlayView!!.setIP(C.ip!!)
        println(C.channel)
        mMediaPlayView!!.setChannel(C.channel!!.toInt())
        mMediaPlayView!!.setDisplay(holder)
        println("MoviePlayView before start")
        mMediaPlayView!!.start()
        println("MoviePlayView after start")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) { // TODO Auto-generated method stub
        mMediaPlayView!!.cancel(true)
    }

    companion object {
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }
}