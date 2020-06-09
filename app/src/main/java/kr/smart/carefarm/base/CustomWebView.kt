package kr.smart.carefarm.base

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebSettings
import android.webkit.WebView

class CustomWebView : WebView {
    private var mOnScrollChangedCallback: OnScrollChangedCallback? = null
    private var mOnOverScrolledCallback: OnOverScrolledCallback? = null
    private var mOnTouchEventCallback: OnTouchEventCallback? = null

    constructor(context: Context?) : super(context) {
        setWebView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        setWebView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        setWebView()
    }

    private fun setWebView() {
        this.settings.javaScriptEnabled = true // 자바스크립트 사용
        this.settings.javaScriptCanOpenWindowsAutomatically = true
        this.settings.loadsImagesAutomatically = true
        this.settings.useWideViewPort = true // ViewPort 적용
        this.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        this.settings.setAppCacheEnabled(false)
        this.settings.domStorageEnabled = true
        this.settings.allowFileAccess = true
        this.settings.textZoom = 100 // 텍스트크기 기본으로 적용
        this.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        this.settings.setSupportMultipleWindows(false) // 새창띄우기 (프로토콜이슈)
        this.settings.loadWithOverviewMode = true
        this.settings.pluginState = WebSettings.PluginState.ON
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.settings.safeBrowsingEnabled = true
        }
    }

    interface OnScrollChangedCallback {
        fun onScroll(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int)
    }

    interface OnOverScrolledCallback {
        fun onOverScrolled(
            scrollX: Int,
            scrollY: Int,
            clampedX: Boolean,
            clampedY: Boolean
        )
    }

    interface OnTouchEventCallback {
        fun onTouchEvent(event: MotionEvent?)
    }

    fun setOnScrollChangedCallback(onScrollChangedCallback: OnScrollChangedCallback?) {
        mOnScrollChangedCallback = onScrollChangedCallback
    }

    fun setOnOverScrolled(onOverScrolledCallback: OnOverScrolledCallback?) {
        mOnOverScrolledCallback = onOverScrolledCallback
    }

    fun setOnTouchEvent(onTouchEventCallback: OnTouchEventCallback?) {
        mOnTouchEventCallback = onTouchEventCallback
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback!!.onScroll(l, t, oldl, oldt)
        }
        super.onScrollChanged(l, t, oldl, oldt)
    }

    override fun onOverScrolled(
        scrollX: Int,
        scrollY: Int,
        clampedX: Boolean,
        clampedY: Boolean
    ) {
        if (mOnOverScrolledCallback != null) {
            mOnOverScrolledCallback!!.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mOnTouchEventCallback != null) {
            mOnTouchEventCallback!!.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }
}