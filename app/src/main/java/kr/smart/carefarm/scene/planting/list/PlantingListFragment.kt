package kr.smart.carefarm.scene.planting.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_planting_list.*
import kr.smart.carefarm.R
import kr.smart.carefarm.base.BaseBindingFragment
import kr.smart.carefarm.base.BaseFragment
import kr.smart.carefarm.config.C
import kr.smart.carefarm.databinding.FragmentPlantingListBinding
import kr.smart.carefarm.model.FarmData
import kr.smart.carefarm.model.PlantingData
import kr.smart.carefarm.scene.cctv.CCTVActivity
import kr.smart.carefarm.scene.planting.PlantingActivity
import kr.smart.carefarm.utils.L


class PlantingListFragment : BaseBindingFragment<FragmentPlantingListBinding>(R.layout.fragment_planting_list) {
    private lateinit var viewModel: PlantingListViewModel
    private var root: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root?.let {
//            initDataBinding()
            return root
        }
        super.onCreateView(inflater, container, savedInstanceState)

        initDataBinding()
        root = binding.root
        return root
//        root?.let {
//            initDataBinding()
//            return root
//        }
//        root = inflater.inflate(R.layout.fragment_planting_list, container, false)
//        initDataBinding()
//        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        L.d("test############################# 2")

        setupListView()


//        arguments?.let { bundle ->
//
//            // 리스트로부터 받은 데이터
//            val data = bundle.getBoolean(C.INTENT_RELOAD)
//            L.d("test!!!!#########3 : ${data}")
//            data?.let {
//                if(it) {
                    val act =(this.activity as? PlantingActivity)
                    act?.reloadList()
//                }
//            }
//        }
    }

    fun setFarmId(farmId: String) {
        viewModel.farmId = farmId
    }

    fun getList(data: List<PlantingData>) {
        viewModel.plantingList.onNext(data)
    }

    private fun initDataBinding() {
        context?.let {context ->
            viewModel = PlantingListViewModel(context)
            binding.view = this
            binding.viewModel = viewModel

            viewModel.plantingList
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    setupList(it)
                }.apply { disposables.add(this) }

            viewModel.moveDetail
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe( {
                    var bundle = Bundle()
                    bundle.putParcelable(C.INTENT_PLANTING_DETAIL, it)
                    navController.navigate(R.id.action_plantingListFragment_to_plantingDetailFragment, bundle)
                }, {
                    L.d("test11111 error : ${it.message}")
                }).apply { disposables.add(this) }

        }

    }

    private fun setupListView() {
        activity?.let {
            list_planting.adapter?.let {
                return
            }
            val adapter = PlantingListAdapter(it, viewModel)
            list_planting.apply {
                //                    this.layoutManager = LinearLayoutManager(it)
                this.adapter = adapter
            }
        }
    }


    private fun setupList(list: List<PlantingData>) {
        (list_planting.adapter as PlantingListAdapter).run {
            this.setPlantingList(list)
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_cctv -> {
                val intent = Intent(context, CCTVActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra(C.INTENT_URL_STRING, C.BASE_URL+"farmCctvList.do?farmId="+viewModel.farmId)
                //test
//                intent.putExtra(C.INTENT_URL_CCTV_FARM_ID, "25")
//        intent.putExtra(C.INTENT_URL_STRING, "")

                startActivity(intent)
            }
        }
    }

}