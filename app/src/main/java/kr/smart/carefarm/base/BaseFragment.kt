package kr.smart.carefarm.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.squareup.otto.Subscribe
import io.reactivex.disposables.CompositeDisposable
import kr.smart.carefarm.utils.ActivityResultEvent
import kr.smart.carefarm.utils.BusProvider
import kr.smart.carefarm.utils.LoadingDialog


open class BaseFragment : Fragment() {
    internal val TAG = BaseFragment::class.java.simpleName

    internal lateinit var loading: LoadingDialog
    internal lateinit var navController: NavController

    internal val disposables by lazy {
        CompositeDisposable()
    }

    private lateinit var callback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBackButtonDispatcher()
    }

    private fun setBackButtonDispatcher() {
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    open fun onBackPressed() {
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BusProvider.instance.register(this)

        navController = Navigation.findNavController(view)
        context?.let {
            loading = LoadingDialog(it)
        }
    }

    override fun onDestroyView() {
        BusProvider.instance.unregister(this)
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (!disposables.isDisposed()) {
            disposables.dispose()
        }
        super.onDestroy()
    }

    @Subscribe
    public fun onActivityResult(activityResultEvent: ActivityResultEvent){
        onActivityResult(activityResultEvent.requestCode, activityResultEvent.resultCode, activityResultEvent.intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }
}