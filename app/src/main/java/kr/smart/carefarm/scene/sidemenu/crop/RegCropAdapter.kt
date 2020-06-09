package kr.smart.carefarm.scene.sidemenu.crop

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import kr.smart.carefarm.databinding.ItemFarmBinding
import kr.smart.carefarm.databinding.ItemRegCropBinding
import kr.smart.carefarm.model.FarmData
import kr.smart.carefarm.model.CropData
import kr.smart.carefarm.scene.farm.FarmFragmentViewModel
import kr.smart.carefarm.utils.L


class RegCropAdapter(private val activity: Activity, private val viewModel: RegCropViewModel) : RecyclerView.Adapter<RegCropAdapter.RegCropViewHolder>() {
    private var items = ArrayList<CropData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegCropViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRegCropBinding.inflate(
            inflater,
            parent,
            false)

        return RegCropViewHolder(
            activity,
            viewModel,
            binding
        )
    }

    fun setRegCropList(userList: List<CropData>?) {
        userList?.let {
            items = ArrayList(userList)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RegCropViewHolder, position: Int) {
        holder.bindRegCrop(items[position], position)
    }

//    companion object {
//        @BindingAdapter("item")
//        @JvmStatic
//        fun bindItem(recyclerView: RecyclerView, items: ObservableArrayList<UserData>) {
//            val adapter: RegCropAdapter? = recyclerView.adapter as? RegCropAdapter
//            // 생략
//            if (adapter != null) {
//                adapter.setRegCropList(items)
//            }
//        }
//    }

    class RegCropViewHolder constructor(private val activity: Activity, private val viewModel: RegCropViewModel, private var mItemRegCropBinding: ItemRegCropBinding): RecyclerView.ViewHolder(mItemRegCropBinding.itemRegCrop) {
        fun bindRegCrop(data: CropData, position: Int) {
            mItemRegCropBinding.setVariable(BR.holder,this)
            mItemRegCropBinding.setVariable(BR.data, data)
            mItemRegCropBinding.setVariable(BR.cropTitle, "${data.cropNm} | ${data.growfacDivn}")
            mItemRegCropBinding.setVariable(BR.areaGrow, "${data.areaGrow}m²")


//            var workList = ArrayList<CropDetailData>()
//            var regList = ArrayList<CropData>()
//            for (i in  0..10){
//                val data = CropDetailData("id","작업명${i}","작업시기 작업${i}")
//                workList.add(data)
//            }
//
//
//            for (i in 0..10){
//                val regCropList = RegCropData("농작물명 ${i}","단동/연","평수",workList,"2020.01.01")
//                regList.add(regCropList)
//            }


        }


        fun onItemClick(data: CropData) {
            L.d("test111 55")
//            viewModel.moveSubAct.onNext(data)
//            CFApplication.prefs.userId = data.userId
//            CFApplication.prefs.userName = data.userNm

            viewModel.sbjMoveWorkCycle.onNext(data)
        }
    }
}