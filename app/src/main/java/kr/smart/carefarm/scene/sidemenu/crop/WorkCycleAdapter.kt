package kr.smart.carefarm.scene.sidemenu.crop

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import kr.smart.carefarm.databinding.ItemWorkCycleBinding
import kr.smart.carefarm.model.CropDetailData
import kr.smart.carefarm.utils.L


class WorkCycleAdapter(private val activity: Activity, private val viewModel: WorkCycleViewModel) : RecyclerView.Adapter<WorkCycleAdapter.WorkCycleViewHolder>() {
    private var items = ArrayList<CropDetailData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkCycleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWorkCycleBinding.inflate(
            inflater,
            parent,
            false)

        return WorkCycleViewHolder(
            activity,
            viewModel,
            binding
        )
    }

    fun setWorkCycleList(userList: List<CropDetailData>?) {
        userList?.let {
            items = ArrayList(userList)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: WorkCycleViewHolder, position: Int) {
        holder.bindWorkCycle(items[position], position)
    }

//    companion object {
//        @BindingAdapter("item")
//        @JvmStatic
//        fun bindItem(recyclerView: RecyclerView, items: ObservableArrayList<UserData>) {
//            val adapter: WorkCycleAdapter? = recyclerView.adapter as? WorkCycleAdapter
//            // 생략
//            if (adapter != null) {
//                adapter.setWorkCycleList(items)
//            }
//        }
//    }

    class WorkCycleViewHolder constructor(private val activity: Activity, private val viewModel: WorkCycleViewModel, private var mItemWorkCycleBinding: ItemWorkCycleBinding): RecyclerView.ViewHolder(mItemWorkCycleBinding.llWorkItem) {
        fun bindWorkCycle(loginItem: CropDetailData, position: Int) {
            mItemWorkCycleBinding.setVariable(BR.holder,this)
            mItemWorkCycleBinding.setVariable(BR.data, loginItem)
            mItemWorkCycleBinding.setVariable(BR.position, position)
        }


        fun onItemClick(data: CropDetailData) {
            L.d("test111 55")
//            viewModel.moveSubAct.onNext(data)
//            CFApplication.prefs.userId = data.userId
//            CFApplication.prefs.userName = data.userNm

//            viewModel.moveMain.onNext(true)
        }
    }
}