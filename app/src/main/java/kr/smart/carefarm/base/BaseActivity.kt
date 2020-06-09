package kr.smart.carefarm.base

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kr.smart.carefarm.utils.ActivityResultEvent
import kr.smart.carefarm.utils.BusProvider
import kr.smart.carefarm.utils.LoadingDialog

open class BaseActivity : AppCompatActivity() {
    internal val TAG = BaseActivity::class.java.simpleName
    internal var loading = LoadingDialog(this)

    internal val disposables by lazy {
        CompositeDisposable()
    }

    override fun onDestroy() {
        if (!disposables.isDisposed()) {
            disposables.dispose()
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        BusProvider.instance.post(ActivityResultEvent(requestCode, resultCode, data))
    }
}