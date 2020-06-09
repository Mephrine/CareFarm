package kr.smart.carefarm.utils

import android.util.Log
import kr.smart.carefarm.application.CFApplication

object L {
    const val TAG = "L"

    fun e(message: String?) {
        if (CFApplication.DEBUG) {
            Log.e(
                TAG,
                Throwable().stackTrace[1].className + "[Line = " + Throwable().stackTrace[1].lineNumber + "]"
            )
            Log.e(TAG, message)
        }
    }

    fun e(tag: String, message: String) {
        if (CFApplication.DEBUG) {
            Log.e(
                TAG,
                Throwable().stackTrace[1].className + "[Line = " + Throwable().stackTrace[1].lineNumber + "]"
            )
            Log.e(TAG, "$tag::$message")
        }
    }

    fun w(message: String?) {
        if (CFApplication.DEBUG) {
            Log.w(TAG, message)
        }
    }

    fun w(tag: String, message: String) {
        if (CFApplication.DEBUG) {
            Log.w(TAG, "$tag::$message")
        }
    }


    fun i(message: String?) {
        if (CFApplication.DEBUG) {
            Log.i(TAG, message)
        }
    }

    fun i(tag: String, message: String) {
        if (CFApplication.DEBUG) {
            Log.i(TAG, "$tag::$message")
        }
    }


    fun d(message: String?) {
        if (CFApplication.DEBUG) {
            Log.d(TAG, message)
        }
    }

    fun d(tag: String, message: String) {
        if (CFApplication.DEBUG) {
            Log.d(TAG, "$tag::$message")
        }
    }


    fun v(message: String?) {
        if (CFApplication.DEBUG) {
            Log.v(TAG, message)
        }
    }

    fun v(tag: String, message: String) {
        if (CFApplication.DEBUG) {
            Log.v(TAG, "$tag::$message")
        }
    }

    fun lc(tag: String, lifecycle: String?) {
        if (CFApplication.DEBUG) {
            if (lifecycle != null) {
                when (lifecycle) {
                    "onCreate" -> Log.v(TAG, "::::::$tag::::::onCreate")
                    "onResume" -> Log.v(TAG, "::::::$tag::::::onResume")
                    "onPause" -> Log.v(TAG, "::::::$tag::::::onPause")
                    "onRestart" -> Log.v(TAG, "::::::$tag::::::onRestart")
                    "onDestroy" -> Log.v(TAG, "::::::$tag::::::onDestroy")
                    "onCreateView" -> Log.v(
                        TAG,
                        "::::::$tag::::::onCreateView"
                    )
                    "onViewCreated" -> Log.v(
                        TAG,
                        "::::::$tag::::::onViewCreated"
                    )
                    "onDestroyView" -> Log.v(
                        TAG,
                        "::::::$tag::::::onDestroyView"
                    )
                }
            }
        }
    }
}
