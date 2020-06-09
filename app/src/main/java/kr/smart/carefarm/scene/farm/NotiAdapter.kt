package kr.smart.carefarm.scene.farm

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import kr.smart.carefarm.databinding.ItemFarmBinding
import kr.smart.carefarm.databinding.ItemNotiBinding
import kr.smart.carefarm.model.FarmData
import kr.smart.carefarm.model.NotiData
import kr.smart.carefarm.utils.L

class NotiAdapter(private val activity: Activity) : RecyclerView.Adapter<NotiAdapter.NotiViewHolder>() {
    private var items = ArrayList<NotiData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNotiBinding.inflate(
            inflater,
            parent,
            false)

        return NotiViewHolder(
            activity,
            binding
        )
    }

    fun setNotiList(userList: List<NotiData>?) {
        userList?.let {
            items = ArrayList(userList)
            L.d("items : ${items}")
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        holder.bindNoti(items[position], position)
    }

//    companion object {
//        @BindingAdapter("item")
//        @JvmStatic
//        fun bindItem(recyclerView: RecyclerView, items: ObservableArrayList<UserData>) {
//            val adapter: NotiAdapter? = recyclerView.adapter as? NotiAdapter
//            // 생략
//            if (adapter != null) {
//                adapter.setNotiList(items)
//            }
//        }
//    }

    class NotiViewHolder constructor(private val activity: Activity, private var mItemNotiBinding: ItemNotiBinding): RecyclerView.ViewHolder(mItemNotiBinding.itemNoti) {
        fun bindNoti(data: NotiData, position: Int) {
            mItemNotiBinding.setVariable(BR.holder,this)
            mItemNotiBinding.setVariable(BR.data, data)


            val notiNm = "${position+1}. ${data.ntcobTitle}"
            mItemNotiBinding.setVariable(BR.notiNm, notiNm)
        }


        fun onItemClick(data: NotiData) {
//            CFApplication.prefs.userId = data.userId
//            CFApplication.prefs.userName = data.userNm

//            viewModel.moveMain.onNext(true)
        }
    }
}