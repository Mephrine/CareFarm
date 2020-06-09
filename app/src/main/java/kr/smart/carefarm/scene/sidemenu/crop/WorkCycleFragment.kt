package kr.smart.carefarm.scene.sidemenu.crop

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_work_cycle.*
import kr.smart.carefarm.R
import kr.smart.carefarm.base.BaseFragment
import kr.smart.carefarm.databinding.FragmentWorkCycleBinding
import kr.smart.carefarm.model.CropDetailData
import kr.smart.carefarm.utils.L
import kr.smart.carefarm.utils.hideKeyboard
import java.util.*


class WorkCycleFragment : BaseFragment() {
    private lateinit var viewModel: WorkCycleViewModel
    private lateinit var binding: FragmentWorkCycleBinding
    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_work_cycle, container, false
        )
        initDataBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { context ->
            viewModel = WorkCycleViewModel(context)
            binding.view = this
            binding.viewModel = viewModel

            setupListView()

            viewModel.sbjRegCrop
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    setupList(it)
                    et_work_nm.setText("")
                    et_work_period.setText("")
                    et_work_content.setText("")
                    bottom_sheet_work_cycle.visibility = View.GONE

                }.apply { disposables.add(this) }

            viewModel.progress
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    loading.show(it)
                }.apply { disposables.add(this) }

            arguments?.let { bundle ->
                // 리스트로부터 받은 데이터
                val arguments = WorkCycleFragmentArgs.fromBundle(bundle)
                viewModel.cropId = arguments.cropId
//                viewModel.sbjRegCrop.onNext(arguments.cropId)
                viewModel.fetchCropDetailList()
            }


        }
    }

    private fun initDataBinding() {


    }

//    private fun showDatePicker() {
//        // DatePickerDialog
//        val cal = Calendar.getInstance()
//        var year = cal.get(Calendar.YEAR)
//        var month = cal.get(Calendar.MONTH)
//        var date = cal.get(Calendar.DATE)
//
//        L.d("test : ${year}.${month}.${date}")
//        // 초기값인 00:00이 아닌 경우, 해당 피커로 보여주기.
//        if (!tv_work_period.text.isEmpty()) {
//            val splitTime = tv_work_period.text.split(".")
//            year = splitTime[0].toInt()
//            month = splitTime[1].toInt() - 1
//            date = splitTime[2].toInt()
//        }
//
//        L.d("test222 : ${year}.${month}.${date}")
//
//        val datePickerDialog = DatePickerDialog(context,
//            OnDateSetListener { view, year, month, dayOfMonth ->
//                tv_work_period.text = "${year}.${month+1}.${dayOfMonth}"
//            }, year, month, date
//        )
//
//        datePickerDialog.setMessage("메시지")
//        datePickerDialog.show()
//    }

    fun onClick(view: View) {
        when(view.id) {
//            R.id.tv_work_period -> {
//                showDatePicker()
//            }
            R.id.ll_btn_arrow -> {
                if (bottom_sheet_work_cycle.isVisible) {
                    bottom_sheet_work_cycle.visibility = View.GONE
                }
            }
            R.id.btn_reg_crop -> {
                if (!bottom_sheet_work_cycle.isVisible) {
                    bottom_sheet_work_cycle.visibility = View.VISIBLE
                } else {
                    // 등록하기
                        viewModel.saveWorkCycleList(et_work_nm.text.toString(), et_work_period.text.toString(), et_work_content.text.toString())

                        et_work_nm.hideKeyboard()
                        et_work_nm.clearFocus()

                        et_work_period.hideKeyboard()
                        et_work_period.clearFocus()

                        et_work_content.hideKeyboard()
                        et_work_content.clearFocus()

                }
            }
        }
    }

    private fun setupListView() {
        activity?.let {
            list_work_cycle.adapter?.let {
                return
            }
            val adapter = WorkCycleAdapter(it, viewModel)
            list_work_cycle.apply {
                //                    this.layoutManager = LinearLayoutManager(it)
                this.adapter = adapter
            }
        }
    }


    private fun setupList(list: List<CropDetailData>) {
        (list_work_cycle.adapter as WorkCycleAdapter).run {
            this.setWorkCycleList(list)
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