package kr.smart.carefarm.scene.login

import android.Manifest
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kr.smart.carefarm.R
import kr.smart.carefarm.api.APIClient
import kr.smart.carefarm.application.CFApplication
import kr.smart.carefarm.base.BaseViewModel
import kr.smart.carefarm.utils.CustomDialog
import kr.smart.carefarm.utils.L
import java.util.ArrayList

class LoginViewModel constructor(private val context: Context) : BaseViewModel() {
//    var moveMain = BehaviorSubject.createDefault(false)
    var moveMain = BehaviorSubject.createDefault<Boolean>(false)
    var token: String? = ""


    fun validation(id: String, pw: String): Boolean {
        if (id.isNotEmpty() && pw.isNotEmpty()) {
            return true
        }

        return false
    }

    fun requestLogin(userId: String, pw: String) {
        progress.onNext(true)

        APIClient().getLoginApi().sendLogin(userId, pw, token ?: "")
            .subscribeOn(Schedulers.io())
            .share()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ patrolResponse ->
                Log.d("tag","response : "+patrolResponse)
                progress.onNext(false)
                if (patrolResponse.code == "00") {
                    CFApplication.prefs.userId = userId
                    moveMain.onNext(true)
                } else {
                    L.d("test3333")
                    var message = patrolResponse.message
                    L.d("test3333 4")
                    if (message.isEmpty()) {
                        L.d("test3333 5")
                        message = context.getString(R.string.fail_login)
                    }
                    L.d("test3333 6")
                    val alert = CustomDialog.Builder(context)
                        .setMassage(message)
                        .setOkayButton(context.getString(R.string.alert_ok))

                    alert.show()
                    L.d("test3333 7")
//                    Toast.makeText(context, R.string.fail_login, Toast.LENGTH_SHORT).show()
                }
            }, { error ->
                progress.onNext(false)
                Log.e("Error : ", error.message)
                error.printStackTrace()
                Toast.makeText(context, "error : ${error.message}", Toast.LENGTH_SHORT).show()
//                Toast.makeText(context, R.string.fail_network_restart, Toast.LENGTH_SHORT).show()
            }).apply { disposables.add(this) }
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) {
            disposables.clear()
        }
    }

}