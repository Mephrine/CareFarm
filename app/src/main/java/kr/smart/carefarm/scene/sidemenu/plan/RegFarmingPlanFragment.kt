package kr.smart.carefarm.scene.sidemenu.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_planting_list.*
import kr.smart.carefarm.R
import kr.smart.carefarm.base.BaseFragment
import kr.smart.carefarm.model.PlantingData
import kr.smart.carefarm.scene.planting.list.PlantingListAdapter
import kr.smart.carefarm.scene.sidemenu.crop.WorkCycleAdapter
import kr.smart.carefarm.scene.sidemenu.crop.WorkCycleViewModel
import kr.smart.carefarm.utils.L


class WorkCycleFragment: BaseFragment() {
    private lateinit var viewModel: WorkCycleViewModel
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
        setupListView()

        arguments?.let {bundle ->
            // 리스트로부터 받은 데이터
            L.d("test111 5")
            context?.let {context ->
                L.d("test111 6")
                var bundle = Bundle()

                viewModel = WorkCycleViewModel(context)
            }
        }
//        layout_patrol.setOnClickListener { navController.navigate(R.id.action_mainFragment_to_patrolFragment) }
//        layout_report.setOnClickListener { navController.navigate(R.id.action_mainFragment_to_reportFragment) }
//        layout_manage.setOnClickListener { navController.navigate(R.id.action_mainFragment_to_trainingFragment) }

    }

    private fun initDataBinding() {

    }

    private fun setupListView() {
        activity?.let {
            list_planting.adapter?.let {
                return
            }
            val adapter = WorkCycleAdapter(it, viewModel)
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
}