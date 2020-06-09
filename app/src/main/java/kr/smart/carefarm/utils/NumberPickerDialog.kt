package kr.smart.carefarm.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import kr.smart.carefarm.R


class NumberPickerDialog : DialogFragment() {
    var valueChangeListener: NumberPicker.OnValueChangeListener? = null
    var title: String? = null   //dialog 제목
    var subtitle: String? = null //dialog 부제목
    var minvalue = 0 //입력가능 최소값 = 0
    var maxvalue = 50 //입력가능 최대값 = 0
    var step = 1 //선택가능 값들의 간격 = 0
    var defvalue = 0 //dialog 시작 숫자 (현재값) = 0


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_number, null)

        val numberPicker = view.findViewById<NumberPicker>(R.id.numberPicker)

        //Dialog 시작 시 bundle로 전달된 값을 받아온다
        title = arguments?.getString("title")
        subtitle = arguments?.getString("subtitle")
        minvalue = arguments?.getInt("minvalue") ?: 30
        maxvalue = arguments?.getInt("maxvalue") ?: 180
        step = arguments?.getInt("step") ?: 30
        defvalue = arguments?.getInt("defvalue") ?: 30
        //최소값과 최대값 사이의 값들 중에서 일정한 step사이즈에 맞는 값들을 배열로 만든다.

        val myValues = getArrayWithSteps(minvalue, maxvalue, step)

        L.d("val### : ${myValues.size}")
        L.d("val### 0  : ${myValues[0]}")
        numberPicker.minValue = 0
        numberPicker.maxValue = (maxvalue - minvalue) / step
        numberPicker.displayedValues = myValues

        //현재값 설정 (dialog를 실행했을 때 시작지점)
        numberPicker.value = (defvalue - minvalue) / step
        //키보드 입력을 방지
        numberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        val builder =
            AlertDialog.Builder(activity)
        builder.setView(view)
        //제목 설정
        builder.setTitle(title)
        //부제목 설정
        builder.setMessage(subtitle)

        builder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                valueChangeListener!!.onValueChange(
                    numberPicker,
                    numberPicker.getValue(), numberPicker.getValue()
                )
            }
        })

        builder.setNegativeButton("CANCEL", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                valueChangeListener!!.onValueChange(
                    numberPicker,
                    numberPicker.getValue(), -99
                )
            }
        })


        return builder.create()
    }


    //최소값부터 최대값가지 일정 간격의 값을 String 배열로 출력
    fun getArrayWithSteps(min: Int, max: Int, step: Int): Array<String?> {
        val number_of_array = (max - min) / step + 1
        val result = arrayOfNulls<String>(number_of_array)
        for (i in 0 until number_of_array) {
            result[i] = (min + step * i).toString()
        }
        return result
    }
}
