package kr.smart.carefarm.utils

import android.content.Intent


data class ActivityResultEvent (val requestCode: Int,
                                val resultCode: Int,
                                val intent: Intent?)

