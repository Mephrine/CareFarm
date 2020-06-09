package kr.smart.carefarm.scene.planting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Timed
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_planting.*

import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.navi_main.*
import kr.smart.carefarm.R
import kr.smart.carefarm.base.BaseActivity
import kr.smart.carefarm.config.C
import kr.smart.carefarm.databinding.ActivityPlantingBinding
import kr.smart.carefarm.model.FarmData
import kr.smart.carefarm.model.NotiData
import kr.smart.carefarm.scene.farm.FarmEventsBus
import kr.smart.carefarm.scene.farm.FarmFragment
import kr.smart.carefarm.scene.farm.NotiAdapter
import kr.smart.carefarm.scene.login.LoginActivity
import kr.smart.carefarm.scene.planting.list.PlantingListFragment
import kr.smart.carefarm.utils.ActivityResultEvent
import kr.smart.carefarm.utils.BusProvider
import kr.smart.carefarm.utils.L
import java.util.concurrent.TimeUnit


class PlantingActivity: BaseActivity() {
    private val EXIT_TIMEOUT: Long = 2000
    private val backButtonClickSource = PublishSubject.create<Boolean>()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var viewModel: PlantingViewModel
    private lateinit var binding: ActivityPlantingBinding

    private lateinit var sheetBehavior: BottomSheetBehavior<View>

    var isFirstAct = false
    var reload = false

    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_planting)

        initDataBinding()

        initDrawer()
        initBottomSheet()
        initView()
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

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.plantingListFragment -> {
                    val listSize = viewModel.notiList.value?.size
                    this.showBottomSheet(listSize ?: 0 > 0)
//                    sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                else -> {
                    sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
//                R.id.patrolFragment -> {
//                    toolbar_title.text = getString(R.string.patrol_bar_title)
//                }
//                R.id.reportFragment -> {
//                    toolbar_title.text = getString(R.string.report_bar_title)
//                }
//                R.id.manageAreaFragment -> {
//                    toolbar_title.text = getString(R.string.manage_bar_title)
//                }
//                R.id.trainingFragment -> {
//                    toolbar_title.text = getString(R.string.training_bar_title)
//                }
            }
        }
    }

    private fun initView(){
        val title = intent.getStringExtra(C.INTENT_TITLE)
        val farmId = intent.getStringExtra(C.INTENT_FARM_ID)
//        val weatherNm = intent.getStringExtra(C.INTENT_WEATHER)
        val isInit = intent.getBooleanExtra(C.INTENT_IS_INIT, false)
//        var notiList = intent.getParcelableArrayListExtra<NotiData>(C.INTENT_NOTI_LIST)

        isFirstAct = isInit
        toolbar_title.text = title

        val bundle = Bundle()
        bundle.putString(C.INTENT_FARM_ID,farmId)
        navController.setGraph(navController.graph,bundle)

        viewModel.fetchNotiList(farmId)

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_planting)
        val farmId = intent.getStringExtra(C.INTENT_FARM_ID)
        L.d("farmId : "+farmId)
        viewModel = PlantingViewModel(this, farmId)


        binding.view = this
        binding.model = viewModel

        navController = Navigation.findNavController(this, R.id.nav_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)

        // Set up ActionBar
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            this.setDisplayShowTitleEnabled(false)
        }

        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        navigation_view.setupWithNavController(navController)

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

        viewModel.plantingList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                L.d("test####### 2 : ${it}")

                (supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment?)?.let {navFrag ->
                    val listFrag = navFrag.childFragmentManager.fragments.first() as? PlantingListFragment

                    L.d("test####### 2222 : ${listFrag}")
                    listFrag?.getList(it)
                    listFrag?.setFarmId(farmId)
                    listFrag?.view
                }

            }).apply { disposables.add(this) }

        viewModel.sbjLogout
            .observeOn(Schedulers.io())
            .distinctUntilChanged()
            .filter{ it == true }
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this.finish()
            })
        viewModel.progress
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                loading.show(it)
            }.apply { disposables.add(this) }


        viewModel.weatherNm
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                tv_weather.text = it
            }.apply { disposables.add(this) }

        // 메인으로 돌아오면 "" <- 리스너에서 처리. 그 외에는 상세하면 이동 시, 이벤트 버스로 타이틀 받아서 적용.
        FarmEventsBus.instance.activityTitleObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                toolbar_title.text = it
            }).apply { disposables.add(this) }

        reload = true
        reloadList()
    }

    internal fun reloadList() {
//        if (reload) {
            viewModel.fetchPlantingList()
            reload = false
//        }
    }

    // Drawer에 표현할 내용 정의
    private fun initDrawer() {
        val version = getVersionInfo()
        tv_app_version.text = version

        val ceoNm = intent.getStringExtra(C.INTENT_CEO_NM)
        tv_user_name.text = ceoNm
    }


    private fun setupListView() {
        val adapter = NotiAdapter(this)
        list_noti.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.setHasFixedSize(true)
            this.adapter = adapter
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            return
        } else {
            if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                return
            } else {
                if (isFirstAct) {
                    if (Navigation.findNavController(this,R.id.nav_fragment)
                            .currentDestination?.id == navController.graph.startDestination) {
                        backButtonClickSource.onNext(true)
                        return
                    }
                }
            }
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        BusProvider.instance.post(ActivityResultEvent(requestCode, resultCode, data))
    }


    fun moveLogin() {
        viewModel.fetchLogout()
    }

    fun moveRegCrop() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        val farmId = intent.getStringExtra(C.INTENT_FARM_ID)

        var bundle = Bundle()
        bundle.putString(C.INTENT_FARM_ID, farmId)
        navController.navigate(R.id.action_plantingListFragment_to_regCropFragment, bundle)
    }

    fun moveFarmingLog() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }

//        val farmData = intent.getParcelableExtra<FarmData>(C.INTENT_DATA)
//        var bundle = Bundle()
//        bundle.putString("detailId", farmData.farmId)
//        navController.navigate(R.id.action_plantingListFragment_to_regCropFragment, bundle)
    }

    fun moveFarmingPlan() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }

//        val farmData = intent.getParcelableExtra<FarmData>(C.INTENT_DATA)
//        var bundle = Bundle()
//        bundle.putString("detailId", farmData.farmId)
//        navController.navigate(R.id.action_plantingListFragment_to_regCropFragment, bundle)
    }


    fun onClick(view: View) {
        when(view.id) {
            R.id.btn_logout -> {
                moveLogin()
            }
            R.id.ll_reg_crop -> {
                moveRegCrop()
            }
            R.id.ll_reg_farming_log -> {
                moveFarmingLog()
            }
            R.id.ll_reg_farming_plan -> {
                moveFarmingPlan()
            }
        }
    }

    fun getVersionInfo(): String {
        val info: PackageInfo = baseContext.packageManager.getPackageInfo(baseContext.packageName, 0)
        return info.versionName
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