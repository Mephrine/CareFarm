package kr.smart.carefarm.base

import android.annotation.TargetApi
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import kr.smart.carefarm.R
import kr.smart.carefarm.utils.L

open class BaseWebViewClient(private val mContext: Context) :
    WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView,
        url: String
    ): Boolean {
        try {
            if (url.startsWith("market://")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                mContext.startActivity(intent)
            } else if (url.startsWith("mailto:")) { //mailto:ironnip@test.com
                val i = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                mContext.startActivity(i)
            } else if (url.startsWith("tel:")) {
                val it = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                mContext.startActivity(it)
            } else if (url.startsWith("smsto:")) {
                val it = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                mContext.startActivity(it)
            } else if (url.startsWith("http://") || url.startsWith("https://")) {
                view.loadUrl(url)
                return true
            } else {
                return true
            }
        } catch (e: Exception) {
            L.e("BaseWebViewClient::shouldOverrideUrlLoading::Exception")
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest
    ): Boolean {
        L.d("BaseWebViewClient::shouldOverrideUrlLoading::" + request.url.toString())
        return shouldOverrideUrlLoading(view, request.url.toString())
    }

    override fun onReceivedSslError(
        view: WebView,
        handler: SslErrorHandler,
        error: SslError
    ) {
        try {
            val mAlertDialog: AlertDialog
            var msg = "보안 인증서가 유효하지 않습니다."
            var flag = false
            when (error.primaryError) {
                SslError.SSL_UNTRUSTED -> {
                    msg = "보안 인증서를 신뢰할 수 없습니다."
                    flag = true
                    L.d("onReceivedSslError@:::$msg")
                }
                SslError.SSL_EXPIRED -> {
                    msg = "보안 인증서가 만료되었습니다."
                    L.d("onReceivedSslError@:::$msg")
                }
                SslError.SSL_IDMISMATCH -> {
                    msg = "보안 인증서 ID가 일치하지 않습니다."
                    L.d("onReceivedSslError@:::$msg")
                }
                SslError.SSL_NOTYETVALID -> {
                    msg = "보안 인증서가 유효하지 않습니다."
                    L.d("onReceivedSslError@:::$msg")
                }
            }
            msg = "$msg\n계속 진행 하시겠습니까?"
            L.d("onReceivedSslError:::$msg")
            if (!flag) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
                builder.setCancelable(false)
                builder.setMessage(msg)
                builder.setNegativeButton(view.context.getString(R.string.picker_cancel), { _, _ ->
                    handler.cancel()
                })
                builder.setPositiveButton(view.context.getString(R.string.alert_ok), { _, _ ->
                    handler.proceed()
                })
                mAlertDialog = builder.create()
                mAlertDialog.show()
            } else {
                handler.cancel()
            }
        } catch (e: Exception) {
            L.e("BaseWebViewClient::onReceivedSslError::Exception")
        }
    }

    override fun onPageStarted(
        view: WebView?,
        url: String?,
        favicon: Bitmap?
    ) {
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
    }

}
