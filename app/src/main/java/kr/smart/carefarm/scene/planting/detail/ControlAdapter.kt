package kr.smart.carefarm.scene.planting.detail

import android.app.Activity
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.kyleduo.switchbutton.SwitchButton
import kotlinx.android.synthetic.main.item_planting_plural.view.*
import kr.smart.carefarm.R
import kr.smart.carefarm.base.BaseFragment
import kr.smart.carefarm.databinding.*
import kr.smart.carefarm.model.ControlData
import kr.smart.carefarm.model.PlantingType
import kr.smart.carefarm.scene.planting.list.PlantingListViewModel
import kr.smart.carefarm.utils.L
import kr.smart.carefarm.utils.NumberPickerDialog
import kr.smart.carefarm.utils.RangeTimePickerDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ControlAdapter(private val fragment: PlantingDetailFragment, private val viewModel: PlantingDetailViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = ArrayList<ControlData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ControlType.Type.SIDE.ordinal -> {
                val binding = ItemSideWindowBinding.inflate(
                    inflater,
                    parent,
                    false)

                return ControlSideWindowViewHolder(
                    fragment,
                    viewModel,
                    binding
                )
            }
            ControlType.Type.TOP.ordinal -> {
                val binding = ItemTopWindowBinding.inflate(
                    inflater,
                    parent,
                    false)

                return ControlTopWindowViewHolder(
                    fragment,
                    viewModel,
                    binding
                )
            }
            else -> {
                val binding = ItemWaterBinding.inflate(
                    inflater,
                    parent,
                    false)

                return ControlWaterViewHolder(
                    fragment,
                    viewModel,
                    binding
                )
            }
        }
    }

    fun setControlList(userList: List<ControlData>?) {
        userList?.let {
            items = ArrayList(userList)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ControlSideWindowViewHolder) {
            holder.bindControl(items[position], position)
        } else if (holder is ControlTopWindowViewHolder) {
            holder.bindControl(items[position], position)
        } else if (holder is ControlWaterViewHolder) {
            holder.bindControl(items[position], position)
        }
    }

    override fun getItemViewType(position: Int): Int {

        val pType = ControlType(items[position].tagDtlDivn)
        return pType.type.ordinal
    }

//    companion object {
//        @BindingAdapter("item")
//        @JvmStatic
//        fun bindItem(recyclerView: RecyclerView, items: ObservableArrayList<UserData>) {
//            val adapter: ControlAdapter? = recyclerView.adapter as? ControlAdapter
//            // 생략
//            if (adapter != null) {
//                adapter.setFarmList(items)
//            }
//        }
//    }

    class ControlSideWindowViewHolder constructor(private val fragment: PlantingDetailFragment, private val viewModel: PlantingDetailViewModel, private var mItemSideWindowBinding: ItemSideWindowBinding): RecyclerView.ViewHolder(mItemSideWindowBinding.itemSideWindow) {
        fun bindControl(item: ControlData, position: Int) {
            mItemSideWindowBinding.setVariable(BR.holder,this)
            mItemSideWindowBinding.setVariable(BR.data, item)

            /*
측창, 천창, 관수 (이하 장비 센서)의 retStatus 값은 장비 센서를 몇 초동안 작동이 되었는지 확인하는 데이터이고 retVal 값은 장비 센서의 물리 상태를 나타냅니다.
retStatus값은 0~180 사이에 위치하고 있으며 java단에서 해당 값을 100분위로 변환하여 뿌려주고 있습니다.
창문의 경우 retVal 값은 1 -> UP, 0 -> STOP, 2 -> DOWN 이며 관수의 경우 retVal 값은 1 -> ON, 0 -> OFF 입니다.
 */
            var stateStr = ""
            var isOpen = false

            when (item.retStatus) {
                "0" -> {
                    stateStr = "닫힘"
                    isOpen = false
                }
                else -> {
                    if (stateStr.equals("0")) {
                        stateStr = "닫힘"
                        isOpen = false
                    } else {
                        stateStr = "열림"
                        isOpen = true
                    }

                }
            }
            var per = item.retStatus ?: "0"
            per.toIntOrNull()?.let {
                if (it > 10) {
                    per = ((it / 10) * 10).toString()
                }
            }

            if (isOpen) {
                mItemSideWindowBinding.tvSideWindowContent.text = per + "% ${stateStr}"
            } else {
                mItemSideWindowBinding.tvSideWindowContent.text = stateStr
            }

            val isNotShow: Boolean = (( viewModel.isOpenOptionEquipId.filter { it.equals(item.farmequiptagId) }.firstOrNull()) ?: "").isEmpty()

            if (isNotShow) {
                mItemSideWindowBinding.clBtn.visibility = View.GONE
                mItemSideWindowBinding.btnControlSetting.text = fragment.getString(R.string.control_btn_setting)
            } else {
                mItemSideWindowBinding.clBtn.visibility = View.VISIBLE
                mItemSideWindowBinding.btnControlSetting.text = fragment.getString(R.string.control_btn_close)
            }

//            mItemSideWindowBinding.tvSideWindowContent.text = stateStr
            setControlling(item.isControlling ?: false)
        }


        fun onItemClick(view: View, data: ControlData) {
            L.d("item name### : ${data.farmequiptagId} | ${data.tagNm}")
            when(view.id) {
                R.id.btn_down -> {
                    viewModel.fetchWindowControl("2" ,data.farmequiptagId, data.retain)
                }
                R.id.btn_stop -> {
                    viewModel.stopControl(data.farmequiptagId)
                }
                R.id.btn_up -> {
                    viewModel.fetchWindowControl("1" ,data.farmequiptagId, data.retain)
                }
            }

        }

        fun showing(data: ControlData) {
            if (mItemSideWindowBinding.clBtn.visibility == View.GONE) {
                mItemSideWindowBinding.clBtn.visibility = View.VISIBLE
                mItemSideWindowBinding.btnControlSetting.text = fragment.getString(R.string.control_btn_close)
                viewModel.isOpenOptionEquipId.add(data.farmequiptagId)
            } else {
                mItemSideWindowBinding.clBtn.visibility = View.GONE
                mItemSideWindowBinding.btnControlSetting.text = fragment.getString(R.string.control_btn_setting)
                viewModel.isOpenOptionEquipId.remove(data.farmequiptagId)
            }
        }

        fun setControlling(isControlling: Boolean) {
            when(isControlling) {
                true -> {
                    mItemSideWindowBinding.btnUp.isChecked = false
                    mItemSideWindowBinding.btnDown.isChecked = false
                    mItemSideWindowBinding.btnStop.isChecked = true

                    mItemSideWindowBinding.btnUp.isEnabled = false
                    mItemSideWindowBinding.btnDown.isEnabled = false
                    mItemSideWindowBinding.btnStop.isEnabled = true

                    mItemSideWindowBinding.tvSideWindowContent.text = "동작 중"
                }
                false -> {
                    mItemSideWindowBinding.btnUp.isChecked = true
                    mItemSideWindowBinding.btnDown.isChecked = true
                    mItemSideWindowBinding.btnStop.isChecked = true

                    mItemSideWindowBinding.btnUp.isEnabled = true
                    mItemSideWindowBinding.btnDown.isEnabled = true
                    mItemSideWindowBinding.btnStop.isEnabled = true
                }
            }
        }
    }

    class ControlTopWindowViewHolder constructor(private val fragment: PlantingDetailFragment, private val viewModel: PlantingDetailViewModel, private var mItemTopWindowBinding: ItemTopWindowBinding): RecyclerView.ViewHolder(mItemTopWindowBinding.itemTopWindow) {
        fun bindControl(item: ControlData, position: Int) {
            mItemTopWindowBinding.setVariable(BR.holder,this)
            mItemTopWindowBinding.setVariable(BR.data, item)
            /*
측창, 천창, 관수 (이하 장비 센서)의 retStatus 값은 장비 센서를 몇 초동안 작동이 되었는지 확인하는 데이터이고 retVal 값은 장비 센서의 물리 상태를 나타냅니다.
retStatus값은 0~180 사이에 위치하고 있으며 java단에서 해당 값을 100분위로 변환하여 뿌려주고 있습니다.
창문의 경우 retVal 값은 1 -> UP, 0 -> STOP, 2 -> DOWN 이며 관수의 경우 retVal 값은 1 -> ON, 0 -> OFF 입니다.

*/
            var stateStr = ""
            var isOpen = false

            when (item.retStatus) {
                "0" -> {
                    stateStr = "닫힘"
                    isOpen = false
                }
                else -> {
                    if (stateStr.equals("0")) {
                        stateStr = "닫힘"
                        isOpen = false
                    } else {
                        stateStr = "열림"
                        isOpen = true
                    }
                }
            }

            var per = item.retStatus ?: "0"
            per.toIntOrNull()?.let {
                if (it > 10) {
                    per = ((it / 10) * 10).toString()
                }
            }

            if (isOpen) {
                mItemTopWindowBinding.tvTopWindowContent.text = per +"% ${stateStr}"
            } else {
                mItemTopWindowBinding.tvTopWindowContent.text = stateStr
            }
//            mItemTopWindowBinding.tvTopWindowContent.text = stateStr

//            viewModel.isOpenOptionEquipId.add(data.farmequiptagId)
            val isNotShow: Boolean = ((viewModel.isOpenOptionEquipId.filter { it.equals(item.farmequiptagId) }.firstOrNull()) ?: "").isEmpty()

            if (isNotShow) {
                mItemTopWindowBinding.clBtn.visibility = View.GONE
                mItemTopWindowBinding.btnControlSetting.text = fragment.getString(R.string.control_btn_setting)
            } else {
                mItemTopWindowBinding.clBtn.visibility = View.VISIBLE
                mItemTopWindowBinding.btnControlSetting.text = fragment.getString(R.string.control_btn_close)
            }


            setControlling(item.isControlling ?: false)
        }


        fun onItemClick(view: View, data: ControlData) {
            L.d("item name### : ${data.farmequiptagId} | ${data.tagNm}")
            when(view.id) {
                R.id.btn_down -> {
                    viewModel.fetchWindowControl("2" ,data.farmequiptagId, data.retain)
                }
                R.id.btn_stop -> {
                    viewModel.stopControl(data.farmequiptagId)
                }
                R.id.btn_up -> {

                    viewModel.fetchWindowControl("1" ,data.farmequiptagId, data.retain)
                }
            }
        }

        fun showing(data: ControlData) {
            if (mItemTopWindowBinding.clBtn.visibility == View.GONE) {
                mItemTopWindowBinding.clBtn.visibility = View.VISIBLE
                mItemTopWindowBinding.btnControlSetting.text = fragment.getString(R.string.control_btn_close)
                viewModel.isOpenOptionEquipId.add(data.farmequiptagId)
            } else {
                mItemTopWindowBinding.clBtn.visibility = View.GONE
                mItemTopWindowBinding.btnControlSetting.text = fragment.getString(R.string.control_btn_setting)
                viewModel.isOpenOptionEquipId.remove(data.farmequiptagId)
            }
        }

        fun setControlling(isControlling: Boolean) {
            when(isControlling) {
                true -> {
                    mItemTopWindowBinding.btnUp.isChecked = false
                    mItemTopWindowBinding.btnDown.isChecked = false
                    mItemTopWindowBinding.btnStop.isChecked = true

                    mItemTopWindowBinding.btnUp.isEnabled = false
                    mItemTopWindowBinding.btnDown.isEnabled = false
                    mItemTopWindowBinding.btnStop.isEnabled = true

                    mItemTopWindowBinding.tvTopWindowContent.text = "동작 중"
                }
                false -> {
                    mItemTopWindowBinding.btnUp.isChecked = true
                    mItemTopWindowBinding.btnDown.isChecked = true
                    mItemTopWindowBinding.btnStop.isChecked = true

                    mItemTopWindowBinding.btnUp.isEnabled = true
                    mItemTopWindowBinding.btnDown.isEnabled = true
                    mItemTopWindowBinding.btnStop.isEnabled = true
                }
            }
        }
    }

    class ControlWaterViewHolder constructor(private val fragment: PlantingDetailFragment, private val viewModel: PlantingDetailViewModel, private var mItemWaterBinding: ItemWaterBinding): RecyclerView.ViewHolder(mItemWaterBinding.itemWater) {
//        var isOpen = false

        fun bindControl(item: ControlData, position: Int) {
            mItemWaterBinding.setVariable(BR.holder,this)
            mItemWaterBinding.setVariable(BR.data, item)
            mItemWaterBinding.setVariable(BR.viewModel, viewModel)

            /*

측창, 천창, 관수 (이하 장비 센서)의 retStatus 값은 장비 센서를 몇 초동안 작동이 되었는지 확인하는 데이터이고 retVal 값은 장비 센서의 물리 상태를 나타냅니다.
retStatus값은 0~180 사이에 위치하고 있으며 java단에서 해당 값을 100분위로 변환하여 뿌려주고 있습니다.
창문의 경우 retVal 값은 1 -> UP, 0 -> STOP, 2 -> DOWN 이며 관수의 경우 retVal 값은 1 -> ON, 0 -> OFF 입니다.

*/

            var isOpen = false
            when (item.retVal) {
                "0" -> {
                    isOpen = false
                }
                "1" -> {
                    isOpen = true
                    if (item.retStatus.equals("0")) {
                        isOpen = false
                    } else {
                        isOpen = true
                    }
                }
            }


//            mItemWaterBinding.switchWatering.isChecked = isOpen
//            if (item.isControlling ?: false) {
//                mItemWaterBinding.tvWateringContent.text = "동작 중"
//            } else {
//                if (isOpen) {
//                    mItemWaterBinding.tvWateringContent.text = (item.retStatus ?: "0") +"분 남음"
//                } else {
//                    mItemWaterBinding.tvWateringContent.text = "미동작"
//                }
//            }

            setControlling(isOpen)
        }

        fun setControlling(isControlling: Boolean) {
            when(isControlling) {
                true -> {
                    mItemWaterBinding.btnUp.isChecked = false
                    mItemWaterBinding.btnStop.isChecked = true

                    mItemWaterBinding.btnUp.isEnabled = false
                    mItemWaterBinding.btnStop.isEnabled = true

                    mItemWaterBinding.tvWateringContent.text = "동작 중"
                }
                false -> {
                    mItemWaterBinding.btnUp.isChecked = true
                    mItemWaterBinding.btnStop.isChecked = true

                    mItemWaterBinding.btnUp.isEnabled = true
                    mItemWaterBinding.btnStop.isEnabled = true

                    mItemWaterBinding.tvWateringContent.text = "미동작"
                }
            }
        }


        fun onItemClick(view: View, data: ControlData) {
//            (view as? SwitchButton)?.let {
                L.d("item name### : ${data.farmequiptagId} | ${data.tagNm}")
                when(view.id) {
                    R.id.btn_up -> {
                        showSetWaterControl(true, data.farmequiptagId, (30 * 60).toString())
                    }
                    R.id.btn_stop -> {
                        showSetWaterControl(false, data.farmequiptagId, "0")
                    }
                }
//                else {
////                    showNumberPicker(view, data)
//                    showSetWaterControl(true, data.farmequiptagId, (30 * 60).toString())
//                }
//                isOpen = !isOpen
//            }
        }

        fun showSetWaterControl(isOn: Boolean, farmequiptagId: String, time: String){
            var msg = ""

            if (isOn) {
                msg = fragment.getString(R.string.control_water_time_apply)
            } else {
                msg = fragment.getString(R.string.control_water_off)
            }

            fragment.view?.context?.let {
                val mAlertDialog: AlertDialog
                val builder: AlertDialog.Builder = AlertDialog.Builder(it)
                builder.setCancelable(false)
                builder.setMessage(msg)
                builder.setNegativeButton(it.getString(R.string.picker_cancel), { _, _ ->
//                    if (!isOn && viewModel.currentWaterState.equals("0")) {
//                        viewModel.sbjWaterState.onNext(true)
//                    }
                    // 원상 복귀.
//                    isOpen = !isOpen
//                    mItemWaterBinding.switchWatering.isChecked = isOpen

                })
                builder.setPositiveButton(it.getString(R.string.alert_ok), { _, _ ->
                    if (isOn) {
                        viewModel.fetchWaterControl(farmequiptagId, time)
                    } else {
                        viewModel.stopControl(farmequiptagId)
                    }

                })
                mAlertDialog = builder.create()
                mAlertDialog.show()
            }
        }


//        fun showWaterDatePicker(view: View, data: ControlData) {
//            val cal = Calendar.getInstance()
//            var min = 0
//
//            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
//                cal.set(Calendar.HOUR_OF_DAY, 0)
//                cal.set(Calendar.MINUTE, minute)
//                cal.set(Calendar.SECOND, 0)
//
//                val selectedTime = SimpleDateFormat("HH:mm:ss").format(cal.time)
//                showSetWaterControl(true, data.farmequiptagId, selectedTime)
//
//            }
//
//            fragment.context?.let {
//                val timePicker = RangeTimePickerDialog(
//                    it,
//                    timeSetListener,
//                    0,
//                    min,
//                    true
//                )
//                timePicker.setOnCancelListener {
//                    isOpen = !isOpen
//                    mItemWaterBinding.switchWatering.isChecked = isOpen
//                }
//
//                timePicker.window.setBackgroundDrawableResource(kr.smart.carefarm.R.color.transparent)
//                timePicker.show()
//            }
//        }

//        fun showNumberPicker(view: View, data: ControlData){
//            val newFragment = NumberPickerDialog()
//            //Dialog에는 bundle을 이용해서 파라미터를 전달한다
//            val bundle = Bundle(6) // 파라미터는 전달할 데이터 개수
//
//            bundle.putString("title", "동작 시간 선택") // key , value
//            bundle.putString("subtitle", "진행할 시간(초)를 선택해주세요.") // key , value
//            bundle.putInt("maxvalue", 180) // key , value
//            bundle.putInt("minvalue", 30) // key , value
//            bundle.putInt("step", 30) // key , value
//            bundle.putInt("defvalue", 30) // key , value
//
//            newFragment.arguments = bundle
//            //class 자신을 Listener로 설정한다
//
//            newFragment.valueChangeListener = NumberPicker.OnValueChangeListener { picker, i1, i2 ->
//                val setting_value = 0 + picker.getValue()*1
//                L.d("setting_value : ${setting_value} | ${i1} | ${i2}")
//
//                // 취소 버튼
//                if(i2 == -99) {
//                    isOpen = !isOpen
//                    mItemWaterBinding.switchWatering.isChecked = isOpen
//                } else {
//                    showSetWaterControl(true, data.farmequiptagId, setting_value.toString())
//                }
//            }
//
//            fragment.fragmentManager?.let {
//                newFragment.show(it, "number picker")
//            }
//        }


    }
}