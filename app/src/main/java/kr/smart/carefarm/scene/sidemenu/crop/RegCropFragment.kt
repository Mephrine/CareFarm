package kr.smart.carefarm.scene.sidemenu.crop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_reg_crop.*
import kr.smart.carefarm.R
import kr.smart.carefarm.base.BaseFragment
import kr.smart.carefarm.config.C
import kr.smart.carefarm.model.CropData
import kr.smart.carefarm.scene.planting.PlantingActivity
import kr.smart.carefarm.utils.L


class RegCropFragment: BaseFragment() {
    private lateinit var viewModel: RegCropViewModel
    private var root: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root?.let {
            initDataBinding()
            return root
        }
        root = inflater.inflate(R.layout.fragment_reg_crop, container, false)
        initDataBinding()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {context ->
            viewModel = RegCropViewModel(context)

            setupListView()

            viewModel.sbjRegCrop
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    setupList(it)
                }.apply { disposables.add(this) }

            viewModel.sbjMoveWorkCycle
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe( {
                    var bundle = Bundle()
                    bundle.putString("cropId", it.cropId)
                    navController.navigate(R.id.action_regCropFragment_to_workCycleFragment, bundle)
                }, {
                    L.d("test11111 error : ${it.message}")
                }).apply { disposables.add(this) }

            viewModel.progress
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    loading.show(it)
                }.apply { disposables.add(this) }


            arguments?.let {bundle ->
                // 리스트로부터 받은 데이터
                val data = bundle.getString(C.INTENT_FARM_ID)
                data?.let {
                    viewModel.fetchRegCropList(it)
                }
            }
        }
    }

    private fun initDataBinding() {

    }

    private fun setupListView() {
        activity?.let {
            list_reg_crop.adapter?.let {
                return
            }
            val adapter = RegCropAdapter(it, viewModel)
            list_reg_crop.apply {
                //                    this.layoutManager = LinearLayoutManager(it)
                this.adapter = adapter
            }
        }
    }


    private fun setupList(list: List<CropData>) {
        (list_reg_crop.adapter as RegCropAdapter).run {
            this.setRegCropList(list)
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        L.d("test3333##########3333##")
        backefreshList()
    }

    fun backefreshList() {
        navController.popBackStack()
    }
}
