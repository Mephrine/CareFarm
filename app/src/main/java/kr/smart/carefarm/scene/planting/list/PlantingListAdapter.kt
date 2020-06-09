package kr.smart.carefarm.scene.planting.list

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_planting_plural.view.*
import kr.smart.carefarm.R
import kr.smart.carefarm.databinding.ItemPlantingPluralBinding
import kr.smart.carefarm.databinding.ItemPlantingPluralBindingImpl
import kr.smart.carefarm.databinding.ItemPlantingSingleBinding
import kr.smart.carefarm.model.PlantingData
import kr.smart.carefarm.model.PlantingType


class PlantingListAdapter(private val activity: Activity, private val viewModel: PlantingListViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = ArrayList<PlantingData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            PlantingType.Type.SINGLE.ordinal -> {
                val binding = ItemPlantingSingleBinding.inflate(
                    inflater,
                    parent,
                    false)

                return PlantingListSingleViewHolder(
                    activity,
                    viewModel,
                    binding
                )
            }
            PlantingType.Type.PLURAL.ordinal -> {
                val binding = ItemPlantingPluralBinding.inflate(
                    inflater,
                    parent,
                    false)

                return PlantingListPluralViewHolder(
                    activity,
                    viewModel,
                    binding
                )
            }
            else -> {
                val binding = ItemPlantingSingleBinding.inflate(
                    inflater,
                    parent,
                    false)

                return PlantingListSingleViewHolder(
                    activity,
                    viewModel,
                    binding
                )
            }
        }
    }

    fun setPlantingList(userList: List<PlantingData>?) {
        userList?.let {
            items = ArrayList(userList)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PlantingListSingleViewHolder) {
            holder.bindPlant(items[position], position)
        } else if (holder is PlantingListPluralViewHolder) {
            holder.bindPlant(items[position], position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val pType = PlantingType(items[position].growfacNm)
        return pType.type.ordinal
    }

//    companion object {
//        @BindingAdapter("item")
//        @JvmStatic
//        fun bindItem(recyclerView: RecyclerView, items: ObservableArrayList<UserData>) {
//            val adapter: PlantingListAdapter? = recyclerView.adapter as? PlantingListAdapter
//            // 생략
//            if (adapter != null) {
//                adapter.setFarmList(items)
//            }
//        }
//    }

    class PlantingListSingleViewHolder constructor(private val activity: Activity, private val viewModel: PlantingListViewModel, private var mItemSingleBinding: ItemPlantingSingleBinding): RecyclerView.ViewHolder(mItemSingleBinding.itemPlantingSingle) {
        fun bindPlant(item: PlantingData, position: Int) {
            mItemSingleBinding.setVariable(BR.holder,this)
            mItemSingleBinding.setVariable(BR.data, item)


            val pType = PlantingType(item.growfacNm)
            when (pType.type) {
                PlantingType.Type.SINGLE -> {

                }
                PlantingType.Type.PLURAL -> {

                }
            }

            val title = "${position}동(${activity.resources.getString(pType.typeNmResource)})"
            mItemSingleBinding.setVariable(BR.plantingNm, title)

            this.itemView.ll_content.removeAllViews()

            // 천창
            for (windowItem in item.windowUTagList ?: emptyList()) {
                val itemView = LayoutInflater.from(activity).inflate(
                    R.layout.item_planting_list_text, null
                )

                val tvContent = itemView.findViewById<TextView>(R.id.tv_content)
                var per = windowItem.retStatus ?: "0"
                // 0 -> off
                if (per.equals("0")) {
                    tvContent.setText("${windowItem.tagNm} : 닫힘")
                } else {
                    per.toIntOrNull()?.let {
                        if (it > 10) {
                            per = ((it / 10) * 10).toString()
                        }
                    }

                    tvContent.setText("${windowItem.tagNm} : ${per}% 열림")
                }

                (tvContent.parent as? ViewGroup)?.removeView(tvContent)
                this.itemView.ll_content.addView(tvContent)

            }

            // 측창
            for (windowItem in item.windowSTagList ?: emptyList()) {
                val itemView = LayoutInflater.from(activity).inflate(
                    R.layout.item_planting_list_text, null
                )

                val tvContent = itemView.findViewById<TextView>(R.id.tv_content)
                var per = windowItem.retStatus ?: "0"
                // 0 -> off
                if (windowItem.retStatus.equals("0")) {
                    tvContent.setText("${windowItem.tagNm} : 닫힘")
                } else {
                    per.toIntOrNull()?.let {
                        if (it > 10) {
                            per = ((it / 10) * 10).toString()
                        }
                    }

                    tvContent.setText("${windowItem.tagNm} : ${per}% 열림")
                }

                (tvContent.parent as? ViewGroup)?.removeView(tvContent)
                this.itemView.ll_content.addView(tvContent)
            }

            // 관수
            for (windowItem in item.warterTagList ?: emptyList()) {
                val itemView = LayoutInflater.from(activity).inflate(
                    R.layout.item_planting_list_text, null
                )

                val tvContent = itemView.findViewById<TextView>(R.id.tv_content)

                // 0 -> off
                if (windowItem.retVal.equals("0")) {
                    tvContent.setText("${windowItem.tagNm} : 미동작")
                } else {
                    tvContent.setText("${windowItem.tagNm} : ${windowItem.retStatus ?: "0"}분 남음")
                }
//                tvContent.setText("${windowItem.tagNm} : ${windowItem.retStatus ?: "0"}")
                (tvContent.parent as? ViewGroup)?.removeView(tvContent)
                this.itemView.ll_content.addView(tvContent)
            }

        }


        fun onItemClick(data: PlantingData) {
//            CFApplication.prefs.userId = data.userId
//            CFApplication.prefs.userName = data.userNm

//            viewModel.moveMain.onNext(true)
            viewModel.moveDetail.onNext(data)
        }
    }

    class PlantingListPluralViewHolder constructor(private val activity: Activity, private val viewModel: PlantingListViewModel, private var mItemFluralBinding: ItemPlantingPluralBinding): RecyclerView.ViewHolder(mItemFluralBinding.itemPlantingPlural) {
        fun bindPlant(item: PlantingData, position: Int) {
            mItemFluralBinding.setVariable(BR.holder,this)
            mItemFluralBinding.setVariable(BR.data, item)


            val pType = PlantingType(item.growfacNm)
            when (pType.type) {
                PlantingType.Type.SINGLE -> {

                }
                PlantingType.Type.PLURAL -> {

                }
            }

            val title = "${position}동(${activity.resources.getString(pType.typeNmResource)})"
            mItemFluralBinding.setVariable(BR.plantingNm, title)

            this.itemView.ll_content.removeAllViews()
            for (windowItem in item.windowUTagList ?: emptyList()) {
                val itemView = LayoutInflater.from(activity).inflate(
                    R.layout.item_planting_list_text, null
                )

                val tvContent = itemView.findViewById<TextView>(R.id.tv_content)
                var per = windowItem.retStatus ?: "0"
                // 0 -> off
                if (windowItem.retStatus.equals("0")) {
                    tvContent.setText("${windowItem.tagNm} : 닫힘")
                } else {
                    per.toIntOrNull()?.let {
                        if (it > 10) {
                            per = ((it / 10) * 10).toString()
                        }
                    }

                    tvContent.setText("${windowItem.tagNm} : ${per}% 열림")
                }

                (tvContent.parent as? ViewGroup)?.removeView(tvContent)
                this.itemView.ll_content.addView(tvContent)

            }

            for (windowItem in item.windowSTagList ?: emptyList()) {
                val itemView = LayoutInflater.from(activity).inflate(
                    R.layout.item_planting_list_text, null
                )

                val tvContent = itemView.findViewById<TextView>(R.id.tv_content)
                var per = windowItem.retStatus ?: "0"
                // 0 -> off
                if (windowItem.retStatus.equals("0")) {
                    tvContent.setText("${windowItem.tagNm} : 닫힘")
                } else {
                    per.toIntOrNull()?.let {
                        if (it > 10) {
                            per = ((it / 10) * 10).toString()
                        }
                    }

                    tvContent.setText("${windowItem.tagNm} : ${per}% 열림")
                }
                (tvContent.parent as? ViewGroup)?.removeView(tvContent)
                this.itemView.ll_content.addView(tvContent)
            }

            for (windowItem in item.warterTagList ?: emptyList()) {
                val itemView = LayoutInflater.from(activity).inflate(
                    R.layout.item_planting_list_text, null
                )

                val tvContent = itemView.findViewById<TextView>(R.id.tv_content)
                // 0 -> off
                if (windowItem.retVal.equals("0")) {
                    tvContent.setText("${windowItem.tagNm} : 미동작")
                } else {
                    tvContent.setText("${windowItem.tagNm} : ${windowItem.retStatus ?: "0"}분 남음")
                }
                (tvContent.parent as? ViewGroup)?.removeView(tvContent)
                this.itemView.ll_content.addView(tvContent)
            }
        }


        fun onItemClick(data: PlantingData) {
//            CFApplication.prefs.userId = data.userId
//            CFApplication.prefs.userName = data.userNm

            viewModel.moveDetail.onNext(data)
        }
    }
}