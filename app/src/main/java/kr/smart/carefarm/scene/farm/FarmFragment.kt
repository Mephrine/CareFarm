package kr.smart.carefarm.scene.farm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_farm.*
import kr.smart.carefarm.R
import kr.smart.carefarm.base.BaseFragment
import kr.smart.carefarm.config.C
import kr.smart.carefarm.model.FarmData
import kr.smart.carefarm.model.NotiData
import kr.smart.carefarm.scene.planting.PlantingActivity
import kr.smart.carefarm.utils.L
import kr.smart.carefarm.utils.LoadingDialog


class FarmFragment : Fragment() {
    private lateinit var viewModel: FarmFragmentViewModel
    internal lateinit var loading: LoadingDialog

    internal val disposables by lazy {
        CompositeDisposable()
    }

    private var root: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root?.let {
            initDataBinding()
            return root
        }
        root = inflater.inflate(R.layout.fragment_farm, container, false)
        initDataBinding()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListView()
    }

    private fun initDataBinding() {
        viewModel = FarmFragmentViewModel()

        viewModel.moveSubAct
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe( {
                context?.let {context ->
                    val intent = Intent(context, PlantingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra(C.INTENT_TITLE, it.farmNm)
                    intent.putExtra(C.INTENT_FARM_ID, it.farmId)
                    intent.putExtra(C.INTENT_CEO_NM, it.ceoNm)


//                    var notiList = ArrayList<NotiData>()
//                    it.notiList?.let { list ->
//                        if(list.size ?: 0 > 0) {
//                            notiList.addAll(list)
//                        }
//                    }
//                    intent.putParcelableArrayListExtra(C.INTENT_NOTI_LIST, notiList)

                    startActivity(intent)
                }

            }, {
                L.d("test11111 error : ${it.message}")
            }).apply { disposables.add(this) }

        viewModel.progress
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                loading.show(it)
            }.apply { disposables.add(this) }

        viewModel.farmList
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                setupList(it)
            }.apply { disposables.add(this) }
    }

    fun getList(data: List<FarmData>) {
        viewModel.farmList.onNext(data)
    }

    private fun setupListView() {
        activity?.let {
            list_farm.adapter?.let {
                return
            }
            val adapter = FarmAdapter(it, viewModel)
            list_farm.apply {
                this.layoutManager = GridLayoutManager(it, 2)
                this.adapter = adapter
            }
        }
    }


    private fun setupList(list: List<FarmData>) {
        (list_farm.adapter as FarmAdapter).run {
            this.setFarmList(list)
        }
        // 1개이면 바로 이동 시키기.
        if (list.size == 1) {
            val intent = Intent(context, PlantingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(C.INTENT_TITLE, list.first().farmNm)
            intent.putExtra(C.INTENT_FARM_ID, list.first().farmId)
            intent.putExtra(C.INTENT_CEO_NM, list.first().ceoNm)

//            if(list[0].notiList?.size ?: 0 > 0) {
//                intent.putParcelableArrayListExtra(C.INTENT_NOTI_LIST, ArrayList(list[0].notiList!!))
//            } else {
//                intent.putParcelableArrayListExtra(C.INTENT_NOTI_LIST, ArrayList())
//            }

            intent.putExtra(C.INTENT_IS_INIT, true)
            startActivity(intent)
            activity?.finish()
        }
    }
}