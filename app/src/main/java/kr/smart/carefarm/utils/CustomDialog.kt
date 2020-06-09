package kr.smart.carefarm.utils

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.dialog_custom.view.*
import kr.smart.carefarm.R
import kr.smart.carefarm.databinding.DialogCustomBinding

class CustomDialog(context: Context) : Dialog(context), View.OnClickListener {
    private lateinit var binding: DialogCustomBinding

//    lateinit var title: String
    var msg: String = ""
    var okay: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_custom, null, false
        )
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable())
        window?.setDimAmount(0.7f)
//        binding.title = this.title
        binding.massage = this.msg
        binding.okay = this.okay

        binding.okayButton.setOnClickListener(this)
        setContentView(binding.root)
    }

    class Builder(context: Context) {
        private val dialog = CustomDialog(context)
//        fun setTitle(text: String): Builder {
//            dialog.title = text
//            return this
//        }

        fun setMassage(text: String): Builder {
            dialog.msg = text
            return this
        }

        fun setOkayButton(text: String): Builder {
            dialog.okay = text
            return this
        }

        fun show(): CustomDialog {
            dialog.show()
            return dialog
        }
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id) {
                R.id.okay_button -> {
                    this.dismiss()
                }
            }
        }

    }
}