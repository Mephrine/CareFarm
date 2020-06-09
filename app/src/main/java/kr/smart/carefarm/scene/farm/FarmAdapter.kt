package kr.smart.carefarm.scene.farm

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import kr.smart.carefarm.application.CFApplication
import kr.smart.carefarm.databinding.ItemFarmBinding
import kr.smart.carefarm.model.FarmData
import kr.smart.carefarm.utils.L

class FarmAdapter(private val activity: Activity, private val viewModel: FarmFragmentViewModel) : RecyclerView.Adapter<FarmAdapter.FarmViewHolder>() {
    private var items = ArrayList<FarmData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFarmBinding.inflate(
            inflater,
            parent,
            false)

        return FarmViewHolder(
            activity,
            viewModel,
            binding
        )
    }

    fun setFarmList(userList: List<FarmData>?) {
        userList?.let {
            items = ArrayList(userList)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FarmViewHolder, position: Int) {
        holder.bindFarm(items[position], position)
    }

//    companion object {
//        @BindingAdapter("item")
//        @JvmStatic
//        fun bindItem(recyclerView: RecyclerView, items: ObservableArrayList<UserData>) {
//            val adapter: FarmAdapter? = recyclerView.adapter as? FarmAdapter
//            // 생략
//            if (adapter != null) {
//                adapter.setFarmList(items)
//            }
//        }
//    }

    class FarmViewHolder constructor(private val activity: Activity, private val viewModel: FarmFragmentViewModel, private var mItemFarmBinding: ItemFarmBinding): RecyclerView.ViewHolder(mItemFarmBinding.itemFarm) {
        fun bindFarm(loginItem: FarmData, position: Int) {
            mItemFarmBinding.setVariable(BR.holder,this)
            mItemFarmBinding.setVariable(BR.data, loginItem)
            mItemFarmBinding.setVariable(BR.position, position)
        }


        fun onItemClick(data: FarmData) {
            L.d("test111 55 : " + data)
            viewModel.moveSubAct.onNext(data)
//            CFApplication.prefs.userId = data.userId
//            CFApplication.prefs.userName = data.userNm

//            viewModel.moveMain.onNext(true)
        }
    }
}