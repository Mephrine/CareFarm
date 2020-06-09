package kr.smart.carefarm.base

import android.content.Context
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.*

open class BaseViewModel() : ViewModel() {
    internal var progress = PublishSubject.create<Boolean>()

    internal val disposables by lazy {
        CompositeDisposable()
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) {
            disposables.clear()
        }
    }
}