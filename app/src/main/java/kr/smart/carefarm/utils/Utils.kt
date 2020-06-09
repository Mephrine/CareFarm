package kr.smart.carefarm.utils

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.ByteString
import java.io.File
import java.util.*

class Utils {
    companion object {
        fun getCurrentDateString(): String {
            val sf = java.text.SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA)
            val cal = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            return sf.format(cal.time)
        }
    }
}

fun <T> toRequestBody(data: T): RequestBody? {
    return when (data) {
        is String -> {
            return RequestBody.create(MediaType.parse("text/*"), data)
        }
        is File -> {
            return RequestBody.create(MediaType.parse("text/*"), data)
        }
        is ByteArray -> {
            return RequestBody.create(MediaType.parse("text/*"), data)
        }
        is ByteString -> {
            return RequestBody.create(MediaType.parse("text/*"), data)
        }
        else -> {
            return null
        }
    }
}


fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Activity.hideKeyboard() {
    hideKeyboard(if (currentFocus == null) View(this) else currentFocus!!)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

data class EditTextFlow(
    val query: String,
    val type: Type
) {
    enum class Type { BEFORE, AFTER, ON }
}

// edittext변경 관련 Flowable
fun EditText.addTextWatcher(): Flowable<EditTextFlow> {
    return Flowable.create<EditTextFlow>({ emitter ->
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                emitter.onNext(EditTextFlow(p0.toString(), EditTextFlow.Type.BEFORE))
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                emitter.onNext(EditTextFlow(p0.toString(), EditTextFlow.Type.ON))
            }

            override fun afterTextChanged(p0: Editable?) {
                emitter.onNext(EditTextFlow(p0.toString(), EditTextFlow.Type.AFTER))
            }
        })
    }, BackpressureStrategy.BUFFER)
}



// Typeface xml에서 지정하기 용
abstract class BindingAdapter {
    companion object {
        @BindingAdapter("typeface")
        @JvmStatic
        fun setTypeface(v: TextView, style: String?) {
            when (style) {
                "italic" -> v.setTypeface(null, Typeface.ITALIC)
                "normal" -> v.setTypeface(null, Typeface.NORMAL)
                else -> v.setTypeface(null, Typeface.NORMAL)

            }
        }
    }
}