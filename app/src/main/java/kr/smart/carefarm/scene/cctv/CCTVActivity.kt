package kr.smart.carefarm.scene.cctv

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_cctv.*
import kr.smart.carefarm.R
import kr.smart.carefarm.base.BaseActivity
import kr.smart.carefarm.base.BaseWebChromeClient
import kr.smart.carefarm.base.BaseWebViewClient
import kr.smart.carefarm.config.C
import kr.smart.carefarm.databinding.ActivityCctvBinding
import kr.smart.carefarm.utils.L
import kr.smart.carefarm.utils.LoadingDialog


//import android.app.Activity
//import android.os.Bundle
//import android.view.SurfaceHolder
//import android.view.SurfaceView
//import android.view.View
//import kr.smart.carefarm.R
//import kr.smart.carefarm.config.C
//import kr.smart.carefarm.lib.MoviePlayView
//class CCTVActivity : Activity(), SurfaceHolder.Callback {
//    var mPreview: SurfaceView? = null
//    var mHolder: SurfaceHolder? = null
//    private var mMediaPlayView: MoviePlayView? = null
//    var mode = NONE
//    var posX1 = 0
//    var posX2 = 0
//    var posY1 = 0
//    var posY2 = 0
//    var oldDist = 1f
//    var newDist = 1f
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_cctv)
//        mPreview = findViewById<View>(R.id.surfaceView1) as SurfaceView
//        mPreview?.let {
//            mHolder = it.holder
//            mHolder?.let {holder ->
//                holder.addCallback(this)
//            }
//        }
//
//    }
//
//
//    override fun surfaceChanged(
//        holder: SurfaceHolder, format: Int, width: Int,
//        height: Int
//    ) { // TODO Auto-generated method stub
//        println("width => $width")
//        println("height => $height")
//    }
//
//    override fun surfaceCreated(holder: SurfaceHolder) { // TODO Auto-generated method stub
//        mHolder!!.setFixedSize(720, 405)
//        println("MoviePlayView before")
//        mMediaPlayView = MoviePlayView()
//
//        mMediaPlayView?.let {
//            println("MoviePlayView after")
//            it.setIP(C.ip)
//            System.out.println(C.channel)
//            it.setChannel(C.channel.toInt())
//            it.setDisplay(holder)
//            println("MoviePlayView before start")
//            it.start()
//            println("MoviePlayView after start")
//        }
//
//    }
//
//    override fun surfaceDestroyed(holder: SurfaceHolder) { // TODO Auto-generated method stub
//        mMediaPlayView?.let {
//            it.cancel(true)
//        }
//    }
//
//    companion object {
//        const val NONE = 0
//        const val DRAG = 1
//        const val ZOOM = 2
//    }
//}


class CCTVActivity: BaseActivity() {
    private lateinit var binding: ActivityCctvBinding

    private var webViewChromeClient: CCTVWebViewChromeClient? = null
    private var webViewClient: BaseWebViewClient? = null

    //test
    private var webUrl: String? = C.BASE_URL + "viewApp.do?farmcctvId="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        initView()
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cctv)
        binding.view = this
    }

    private fun initView(){
        try {
            val urlStr = intent.getStringExtra(C.INTENT_URL_STRING)
            val farmCctvId = intent.getStringExtra(C.INTENT_URL_CCTV_FARM_ID)
            webUrl += farmCctvId
            if (!urlStr.isEmpty()) {
                webUrl = urlStr
            }
        } catch (e: Exception) {
            L.e(e.toString())
        }

        webViewChromeClient = CCTVWebViewChromeClient()
        webViewClient = BaseWebViewClient(baseContext)

        L.d("loadUrl : ${webUrl}")

        webViewChromeClient?.let {
            webView.webChromeClient = it
        }
        webViewClient?.let {
            webView.webViewClient = it
        }

        webView.loadUrl(webUrl)
    }
    class CCTVWebViewChromeClient : BaseWebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
            return
        } else {
            scriptStopCCTV()
        }
    }

    fun scriptStopCCTV() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setMessage(getString(R.string.cctv_stop_alert))
        builder.setNegativeButton(getString(android.R.string.cancel), { _, _ ->

        })
        builder.setPositiveButton(getString(android.R.string.ok), { _, _ ->
            webView.loadUrl("javascript:cctvToStop()")
            loading.show(true)

            Handler().postDelayed({
                loading.show(false)
                destroyWebView()
                finish()
            }, 3000)


        })
        val mAlertDialog = builder.create()
        mAlertDialog.show()
    }

    fun destroyWebView() {
        webView.clearHistory()
        webView.clearCache(true)
        webView.loadUrl("about:blank")
        webView.onPause()
        webView.removeAllViews()
        webView.destroyDrawingCache()

        webView.pauseTimers()
        webView.destroy()
    }
}