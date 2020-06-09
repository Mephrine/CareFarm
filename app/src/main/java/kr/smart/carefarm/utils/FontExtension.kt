package kr.smart.carefarm.utils

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import kr.smart.carefarm.R

enum class FontSansType {
    NORMAL, REGULAR
//    DEMI_LIGHT{
//        override fun path(): String {
//            return "/system/font_family/NotoSansCJK-DemiLight.ttc"
//        }
//    },REGULAR {
//        override fun path(): String {
//            return "/system/font_family/NotoSansCJK-Regular.ttc"
//        }
//    };
//
//    abstract fun path(): String
}

fun TextView.setFont(context: Context, type: FontSansType) {
    when(type){
        FontSansType.NORMAL -> {
            this.typeface = ResourcesCompat.getFont(context, R.font.sans_medium)
        }
        FontSansType.REGULAR -> {
            this.typeface = ResourcesCompat.getFont(context, R.font.sans_regular)
        }
    }

    this.includeFontPadding = false
}