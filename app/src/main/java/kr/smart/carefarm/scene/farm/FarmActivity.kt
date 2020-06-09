package kr.smart.carefarm.scene.farm

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Timed
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_farm.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kr.smart.carefarm.R
import kr.smart.carefarm.base.BaseActivity
import kr.smart.carefarm.databinding.ActivityFarmBinding
import kr.smart.carefarm.utils.ActivityResultEvent
import kr.smart.carefarm.utils.BusProvider
import kr.smart.carefarm.utils.L
import java.util.concurrent.TimeUnit


class FarmActivity: BaseActivity() {
    private val EXIT_TIMEOUT: Long = 2000
    private val backButtonClickSource = PublishSubject.create<Boolean>()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var viewModel: FarmViewModel
    private lateinit var binding: ActivityFarmBinding

    private lateinit var sheetBehavior: BottomSheetBehavior<View>

    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_farm)

        initDataBinding()
        initBottomSheet()
        setupListView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed()) {
            disposables.dispose()
        }
    }

    fun initBottomSheet() {
//        val bottomSheetFragment = BottomSheetFragment()
//        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)


        sheetBehavior = BottomSheetBehavior.from(rl_bottom_sheet)


        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        setBtnCloseSheet()
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        setBtnExpandSheet()
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }
        })

        ll_top.setOnClickListener {
            when (sheetBehavior.state) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
                else -> {
                }
            }
        }

        // 처음에 image 180도 회전해서 보이기.
        val deg: Float = img_arrow.getRotation() + 180f
        img_arrow.rotation = deg
    }

    private fun showBottomSheet(show: Boolean) {
        if (show) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_farm)

        viewModel = FarmViewModel(this)

//        viewModel.userName
//            .distinctUntilChanged()
//            .subscribe({
//                this.tv_greet.text = getString(R.string.drawer_greet, it)
//            }).apply { disposables.add(this) }

        binding.view = this
        binding.model = viewModel

        // Set up ActionBar
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            this.setDisplayShowTitleEnabled(false)
        }


        backButtonClickSource
            .debounce(100, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Toast.makeText(
                    this,
                    R.string.app_exit_title,
                    Toast.LENGTH_SHORT
                ).show()
            }
            .timeInterval(TimeUnit.MILLISECONDS)
            .skip(1)
            .filter(Predicate {
                    interval: Timed<Boolean?> -> interval.time() < EXIT_TIMEOUT
            })
            .subscribe(Consumer<Timed<Boolean?>> {
                this.finish()
            }).apply { disposables.add(this) }




        viewModel.notiList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.showBottomSheet(it.size > 0)
                (list_noti.adapter as NotiAdapter).run {
                    this.setNotiList(it)
                }
                tv_noti_title.text = String.format(resources.getString(R.string.main_noti_title),it.size)

            }.apply { disposables.add(this) }

        viewModel.farmList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val frag: FarmFragment? =
                    supportFragmentManager.findFragmentById(R.id.frag_farm) as FarmFragment?
                frag?.getList(it)
            }).apply { disposables.add(this) }



        viewModel.fetchFarmList()
        viewModel.fetchNotiList()
    }


    private fun setupListView() {
        val adapter = NotiAdapter(this)
        list_noti.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.setHasFixedSize(true)
            this.adapter = adapter

        }

    }


    override fun onBackPressed() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            return
        }
        backButtonClickSource.onNext(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        BusProvider.instance.post(ActivityResultEvent(requestCode, resultCode, data))
    }


    private fun setBtnExpandSheet() {
        L.d("Expand sheet")
        //  btnBottomSheet.setText("Expand Sheet");
        if (img_arrow.getRotation() == 0f) { //  btnBottomSheet.setText("Close Sheet");
            val deg: Float = img_arrow.getRotation() + 180f
            img_arrow.animate().rotation(deg).setInterpolator(AccelerateDecelerateInterpolator())
        }
    }

    private fun setBtnCloseSheet() {
        L.d("Close sheet")
        if (img_arrow.getRotation() == 180f) {
            val deg = 0f
            img_arrow.animate().rotation(deg).setInterpolator(AccelerateDecelerateInterpolator())
        }
    }
}