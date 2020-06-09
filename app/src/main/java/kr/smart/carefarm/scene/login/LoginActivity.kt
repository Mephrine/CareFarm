package kr.smart.carefarm.scene.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Timed
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_login.*
import kr.smart.carefarm.R
import kr.smart.carefarm.application.CFApplication
import kr.smart.carefarm.base.BaseActivity
import kr.smart.carefarm.databinding.ActivityLoginBinding
import kr.smart.carefarm.scene.farm.FarmActivity
import kr.smart.carefarm.utils.EditTextFlow
import kr.smart.carefarm.utils.L
import kr.smart.carefarm.utils.LoadingDialog
import kr.smart.carefarm.utils.addTextWatcher
import java.util.concurrent.TimeUnit

class LoginActivity : BaseActivity() {
    private val EXIT_TIMEOUT: Long = 2000
    private val backButtonClickSource = PublishSubject.create<Boolean>()
    private lateinit var binding: ActivityLoginBinding

    private var viewModel = LoginViewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Create channel to show notifications.
//            val channelId = getString(R.string.default_notification_channel_id)
//            val channelName = getString(R.string.default_notification_channel_name)
//            val notificationManager = getSystemService(NotificationManager::class.java)
//            notificationManager?.createNotificationChannel(NotificationChannel(channelId,
//                channelName, NotificationManager.IMPORTANCE_LOW))
//        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        intent.extras?.let {
            for (key in it.keySet()) {
                val value = intent.extras?.get(key)
                Log.d(TAG, "Key: $key Value: $value")
            }
        }
        // [END handle_data_extras]


        // [START subscribe_topics]
//        FirebaseMessaging.getInstance().subscribeToTopic(C.FCM_CHANNEL)
//            .addOnCompleteListener { task ->
//                var msg = getString(R.string.msg_subscribed)
//                if (!task.isSuccessful) {
//                    msg = getString(R.string.msg_subscribe_failed)
//                }
//                Log.d(TAG, msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//            }
//        // [END subscribe_topics]
//

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        initData()
    }


    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.view = this
        binding.viewModel = viewModel

        // 뒤로가기
        backButtonClickSource
            .debounce(100, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Toast.makeText(
                    this,
                    R.string.app_exit_title,
                    Toast.LENGTH_SHORT
                ).show()
            }
            .timeInterval(TimeUnit.MILLISECONDS)
            .skip(1)
            .filter(Predicate { interval: Timed<Boolean?> ->
                interval.time() < EXIT_TIMEOUT
            })
            .subscribe(Consumer<Timed<Boolean?>> {
                this.finish()
            })


        val flowableId = et_email_id.addTextWatcher()
            .subscribeOn(Schedulers.io())
            .filter { it.type == EditTextFlow.Type.AFTER }
            .map { it.query }
            .distinctUntilChanged()
            .toObservable()

        val flowablePw = et_pw.addTextWatcher()
            .subscribeOn(Schedulers.io())
            .filter { it.type == EditTextFlow.Type.AFTER }
            .map { it.query }
            .distinctUntilChanged()
            .toObservable()
//
        Observable.combineLatest(arrayOf(flowableId, flowablePw), {
            viewModel.validation(
                it[0].toString(),
                it[1].toString()
            )
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                btn_login.isEnabled = it
            }.apply { disposables.add(this) }


        viewModel.moveMain.subscribeOn(Schedulers.io())
            .filter { it == true }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                L.d("test111")
                moveMain()
            }.apply { disposables.add(this) }
        viewModel.progress
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                loading.show(it)
            }.apply { disposables.add(this) }


        Observable.just(CFApplication.prefs.autoId)
            .take(1)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
                et_email_id.setText(it)
                chk_id.isChecked = !it.isEmpty()
            }.apply { disposables.add(this) }

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                viewModel.token = task.result?.token
            })
    }

    private fun moveMain() {
        if (chk_id.isChecked) {
            CFApplication.prefs.autoId = et_email_id.text.toString()
        } else {
            CFApplication.prefs.autoId = ""
        }

        val intent = Intent(this, FarmActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
//        overridePendingTransition(R.anim.trans_start_enter, R.anim.trans_start_exit)
        this.finish()
    }


    override fun onBackPressed() {
//        super.onBackPressed()
        backButtonClickSource.onNext(true)
    }

    fun onClick(view: View) {
        L.d("왜 안타요????? 221112")
        when (view.id) {
            R.id.btn_login -> {

                L.d("왜 안타요????? 222")
                et_email_id.clearFocus()
                et_pw.clearFocus()
                viewModel.requestLogin(et_email_id.text.toString(), et_pw.text.toString())
            }
        }
    }

    override fun onDestroy() {
        if (!disposables.isDisposed) {
            disposables.clear()
        }
        super.onDestroy()
    }

}