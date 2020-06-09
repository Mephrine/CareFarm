package kr.smart.carefarm.utils

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import kr.smart.carefarm.R


class LoadingDialog(private val context: Context) {
    var dialog: Dialog? = null

    fun show(show: Boolean) {
        Log.d("load","loading show: "+show)
        context?.let {
            Log.d("load","loading show: 1111")
            if (show) {
                Log.d("load","loading show: 222")
                dialog?.let {
                    it.show()
                    Log.d("load","loading show: 333")
                    return
                }
                Log.d("load","loading show: 444")
                val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflator.inflate(R.layout.view_loading, null)

                dialog = Dialog(context).apply {
                    Log.d("load","loading show: 555")
                    this.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    this.setContentView(view)
                }

                Log.d("load","loading show: 666")
                dialog?.show()

            } else {
                Log.d("load","loading show: 777")
                dialog?.let {
                    Log.d("load","loading show: 888")
                    it.dismiss()
                }
            }
        }

    }
}