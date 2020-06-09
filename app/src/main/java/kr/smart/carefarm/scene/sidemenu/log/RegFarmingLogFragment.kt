package kr.smart.carefarm.scene.sidemenu.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.smart.carefarm.R
import kr.smart.carefarm.base.BaseFragment
import kr.smart.carefarm.utils.L


class RegFarmingLogFragment: BaseFragment() {
    private lateinit var viewModel: RegFarmingLogViewModel
    private var root: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root?.let {
            initDataBinding()
            return root
        }
        root = inflater.inflate(R.layout.fragment_work_cycle, container, false)
        initDataBinding()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        L.d("test111 4")

        arguments?.let {bundle ->
            // 리스트로부터 받은 데이터
            L.d("test111 5")


            context?.let {context ->
                viewModel = RegFarmingLogViewModel(context)
//                val arguments = RegFarmingLogFragmentArgs.formBundle(context)
//                viewModel.farmData.onNext(arguments.data)
//                L.d("test111 6")
//                var bundle = Bundle()
//                val detailId = bundle.getString("detailId")
            }
        }

//        layout_patrol.setOnClickListener { navController.navigate(R.id.action_mainFragment_to_patrolFragment) }
//        layout_report.setOnClickListener { navController.navigate(R.id.action_mainFragment_to_reportFragment) }
//        layout_manage.setOnClickListener { navController.navigate(R.id.action_mainFragment_to_trainingFragment) }

    }

    private fun initDataBinding() {

    }
}