package kr.smart.carefarm.base

import android.R
import android.app.AlertDialog
import android.os.Message
import android.view.View
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebView.WebViewTransport
import kr.smart.carefarm.utils.L


open class BaseWebChromeClient : WebChromeClient() {
    override fun onJsConfirm(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        try {
            val mAlertDialog: AlertDialog
            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
            builder.setCancelable(false)
            builder.setMessage(message)
            builder.setNegativeButton(view.context.getString(R.string.cancel), { _, _ ->
                result.cancel()
            })
            builder.setPositiveButton(view.context.getString(R.string.ok), { _, _ ->
                result.confirm()
            })
            mAlertDialog = builder.create()
            mAlertDialog.show()
        } catch (e: Exception) {
            L.e(e.toString())
        }
        return true
    }

    override fun onJsAlert(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        try {
            val mAlertDialog: AlertDialog
            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
            builder.setCancelable(false)
            builder.setMessage(message)
            builder.setPositiveButton(view.context.getString(R.string.ok), { _, _ ->
                result.confirm()
            })
            mAlertDialog = builder.create()
            mAlertDialog.show()
        } catch (e: Exception) {
            L.e(e.toString())
        }
        return true
    }

    override fun onCreateWindow(
        view: WebView,
        dialog: Boolean,
        userGesture: Boolean,
        resultMsg: Message
    ): Boolean {
        val newWebView = WebView(view.context)
        val transport = resultMsg.obj as WebViewTransport
        transport.webView = newWebView
        resultMsg.sendToTarget()
        return true
    }
}
