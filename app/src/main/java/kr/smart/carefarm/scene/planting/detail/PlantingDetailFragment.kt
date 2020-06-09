package kr.smart.carefarm.scene.planting.detail

import android.Manifest
import android.animation.ValueAnimator
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import com.bumptech.glide.RequestManager
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.jakewharton.rxbinding2.widget.RxTextView
import com.kyleduo.switchbutton.SwitchButton
import gun0912.tedbottompicker.TedRxBottomPicker
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_planting_detail.*
import kotlinx.android.synthetic.main.fragment_planting_detail.tv_alarm_warn_max_value
import kotlinx.android.synthetic.main.fragment_planting_detail.tv_alarm_warn_min_value
import kotlinx.android.synthetic.main.fragment_planting_list.*
import kotlinx.android.synthetic.main.item_crop_list.view.*
import kotlinx.android.synthetic.main.item_planting_plural.view.*
import kr.smart.carefarm.R
import kr.smart.carefarm.base.BaseBindingFragment
import kr.smart.carefarm.base.BaseFragment
import kr.smart.carefarm.config.C
import kr.smart.carefarm.databinding.FragmentPlantingDetailBinding
import kr.smart.carefarm.model.ControlData
import kr.smart.carefarm.model.CropData
import kr.smart.carefarm.model.PlantingData
import kr.smart.carefarm.model.SnsrCriticalData
import kr.smart.carefarm.scene.cctv.CCTVActivity
import kr.smart.carefarm.scene.planting.PlantingActivity
import kr.smart.carefarm.scene.planting.list.PlantingListAdapter
import kr.smart.carefarm.utils.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class PlantingDetailFragment: BaseBindingFragment<FragmentPlantingDetailBinding>(R.layout.fragment_planting_detail) {
    private lateinit var viewModel: PlantingDetailViewModel
    private lateinit var requestManager: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {context ->
            viewModel = PlantingDetailViewModel(context)
            binding.view = this
            binding.viewModel = viewModel

//            initDataBinding()

            arguments?.let {bundle ->
            // 리스트로부터 받은 데이터
                val data = bundle.getParcelable<PlantingData>(C.INTENT_PLANTING_DETAIL)
                data?.let {
                    viewModel.growfacId = it.growfacId
                    viewModel.farmId = it.farmId

                    // 센서 정보
                    if (it.snsrTagList?.size ?: 0 > 0) {
                        val temp = "KH1501"
                        val humid = "KH1502"
                        val co2 = "KH1505"

                        for (item in it.snsrTagList!!) {
                            when (item.tagDtlDivn) {
                                temp -> {
                                    viewModel.sbjTemp.onNext(item.retVal)
                                    tv_temp_cur.text = item.retVal + "°C"
                                }
                                humid -> {
                                    viewModel.sbjHumidity.onNext(item.retVal)
                                    tv_humid_cur.text = item.retVal + "%"
                                }
                                co2 -> {
                                    viewModel.sbjCo2.onNext(item.retVal)
                                    tv_co2_cur.text = item.retVal + "ppm"
                                }
                            }
                        }
                    }

//                    viewModel.reloadControlList(it)
                    setupListView()

                    viewModel.selectedUri.subscribeOn(Schedulers.io())
                        .subscribe({
                            if (it != null && it != Uri.EMPTY) {
//                    requestManager
//                        .load(it)
//                        .into(showImage(true))

                            } else {
//                    showImage(false).setImageResource(R.drawable.ic_gallery)
                            }
                        }) {
                            //                showImage(false).setImageResource(R.drawable.ic_gallery)
                        }
                        .apply { disposables.add(this) }


                    // 센서 통신 완료 후에 3번이 실행되기 때문에, debounce로 첫번째 값만 적용하고 이후 값은 무시하기 위해 사용.
                    val obMinVal = RxTextView.textChanges(tv_alarm_warn_min_value)
                        .subscribeOn(Schedulers.io())
                        .debounce(100, TimeUnit.MILLISECONDS)
                        .map { if (it.isNullOrEmpty()) "0" else { it.toString() } }
                        .map { (it.toLong()).toString() }
                        .share()
                        .distinctUntilChanged()


                    // 센서 통신 완료 후에 3번이 실행되기 때문에, debounce로 첫번째 값만 적용하고 이후 값은 무시하기 위해 사용.
                    val obMaxVal = RxTextView.textChanges(tv_alarm_warn_max_value)
                        .subscribeOn(Schedulers.io())
                        .debounce(100, TimeUnit.MILLISECONDS)
                        .map { if (it.isNullOrEmpty()) "0" else { it.toString() } }
                        .map { (it.toLong()).toString() }
                        .share()
                        .distinctUntilChanged()


                    obMinVal
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            if (ll_sensor_cri.visibility == View.VISIBLE) {
                                when (viewModel.sbjSnsrType.value) {
                                    SnsrType.TEMPER -> {
                                        viewModel.sbjTempCriticalMin.onNext(it)
                                    }
                                    SnsrType.HUMID -> {
                                        viewModel.sbjHumidityCriticalMin.onNext(it)
                                    }
                                    SnsrType.CO2 -> {
                                        viewModel.sbjCo2CriticalMin.onNext(it)
                                    }
                                }
                            }
                        }.apply { disposables.add(this) }

                    obMaxVal
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            if (ll_sensor_cri.visibility == View.VISIBLE) {
                                when (viewModel.sbjSnsrType.value) {
                                    SnsrType.TEMPER -> {
                                        viewModel.sbjTempCriticalMax.onNext(it)
                                    }
                                    SnsrType.HUMID -> {
                                        viewModel.sbjHumidityCriticalMax.onNext(it)
                                    }
                                    SnsrType.CO2 -> {
                                        viewModel.sbjCo2CriticalMax.onNext(it)
                                    }
                                }
                            }
                        }.apply { disposables.add(this) }


                    Observable.combineLatest(arrayOf(obMinVal, obMaxVal), {
                        viewModel.validation(
                            it[0].toString(),
                            it[1].toString()
                        )
                    })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            btn_apply_sensor.isEnabled = it
                            btn_apply_sensor.isChecked = it
                        }.apply { disposables.add(this) }


                    viewModel.sbjControlList
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe {
                            setupList(it)
                        }.apply { disposables.add(this) }

                    tv_temp_max.text = "0°C"
                    tv_temp_min.text = "0°C"

                    viewModel.sbjTempCriticalMax
                        .subscribeOn(Schedulers.io())
                        .distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            tv_temp_max.text = it+"°C"

                            if (viewModel.sbjSnsrType.value == SnsrType.TEMPER && ll_sensor_cri.visibility == View.VISIBLE) {
                                tv_alarm_warn_max_value.setText(it)
                                tv_alarm_warn_max_value.setSelection(tv_alarm_warn_max_value.text.length)
                            }
                        }.apply { disposables.add(this) }

                    viewModel.sbjTempCriticalMin
                        .subscribeOn(Schedulers.io())
                        .distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            tv_temp_min.text = it+"°C"

                            if (viewModel.sbjSnsrType.value == SnsrType.TEMPER && ll_sensor_cri.visibility == View.VISIBLE) {
                                tv_alarm_warn_min_value.setText(it)
                                tv_alarm_warn_min_value.setSelection(tv_alarm_warn_min_value.text.length)
                            }
                        }.apply { disposables.add(this) }

                    Observable.combineLatest(arrayOf(viewModel.sbjTempCriticalMin, viewModel.sbjTemp, viewModel.sbjTempCriticalMax),{
                        viewModel.isBetween(it[0].toString(), it[1].toString(), it[2].toString())
                    }).subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            context?.let {context ->
                                when(it) {
                                    PlantingDetailViewModel.IS_BETWEEN.LOWER ->  {
                                        graph_temp.setSecondTrackColor(ContextCompat.getColor(context, R.color.color_graph_yellow))
                                        graph_temp.setThumbColor(ContextCompat.getColor(context, R.color.color_graph_yellow))
                                        graph_temp.setTrackColor(ContextCompat.getColor(context, R.color.color_graph_yellow))
                                        graph_temp.setProgress(0f)
                                    }

                                    PlantingDetailViewModel.IS_BETWEEN.BETWEEN ->  {
                                        graph_temp.setSecondTrackColor(ContextCompat.getColor(context, R.color.color_graph_green))
                                        graph_temp.setThumbColor(ContextCompat.getColor(context, R.color.color_graph_green))
                                        graph_temp.setTrackColor(ContextCompat.getColor(context, R.color.color_graph_green))
                                        graph_temp.setProgress(1f)
                                    }

                                    PlantingDetailViewModel.IS_BETWEEN.UPPER ->  {
                                        graph_temp.setSecondTrackColor(ContextCompat.getColor(context, R.color.color_graph_red))
                                        graph_temp.setThumbColor(ContextCompat.getColor(context, R.color.color_graph_red))
                                        graph_temp.setTrackColor(ContextCompat.getColor(context, R.color.color_graph_red))
                                        graph_temp.setProgress(2f)
                                    }
                                }
                            }
                        }.apply { disposables.add(this) }

                    tv_humid_min.text = "0%"
                    tv_humid_max.text = "0%"

                    viewModel.sbjHumidityCriticalMax
                        .subscribeOn(Schedulers.io())
                        .distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            tv_humid_max.text = it+"%"

                            if (viewModel.sbjSnsrType.value == SnsrType.HUMID && ll_sensor_cri.visibility == View.VISIBLE) {
                                tv_alarm_warn_min_value.setText(it)
                                tv_alarm_warn_min_value.setSelection(tv_alarm_warn_min_value.text.length)
                            }

                        }.apply { disposables.add(this) }



                    viewModel.sbjHumidityCriticalMin
                        .subscribeOn(Schedulers.io())
                        .distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            tv_humid_min.text = it+"%"

                            if (viewModel.sbjSnsrType.value == SnsrType.HUMID && ll_sensor_cri.visibility == View.VISIBLE) {
                                tv_alarm_warn_min_value.setText(it)
                                tv_alarm_warn_min_value.setSelection(tv_alarm_warn_min_value.text.length)
                            }
                        }.apply { disposables.add(this) }

                    Observable.combineLatest(arrayOf(viewModel.sbjHumidityCriticalMin, viewModel.sbjHumidity, viewModel.sbjHumidityCriticalMax),{
                        viewModel.isBetween(it[0].toString(), it[1].toString(), it[2].toString())
                    }).subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            context?.let {context ->
                                when(it) {
                                    PlantingDetailViewModel.IS_BETWEEN.LOWER ->  {
                                        graph_humid.setSecondTrackColor(ContextCompat.getColor(context, R.color.color_graph_yellow))
                                        graph_humid.setThumbColor(ContextCompat.getColor(context, R.color.color_graph_yellow))
                                        graph_humid.setTrackColor(ContextCompat.getColor(context, R.color.color_graph_yellow))
                                        graph_humid.setProgress(0f)
                                    }

                                    PlantingDetailViewModel.IS_BETWEEN.BETWEEN ->  {
                                        graph_humid.setSecondTrackColor(ContextCompat.getColor(context, R.color.color_graph_green))
                                        graph_humid.setThumbColor(ContextCompat.getColor(context, R.color.color_graph_green))
                                        graph_humid.setTrackColor(ContextCompat.getColor(context, R.color.color_graph_green))
                                        graph_humid.setProgress(1f)
                                    }

                                    PlantingDetailViewModel.IS_BETWEEN.UPPER ->  {
                                        graph_humid.setSecondTrackColor(ContextCompat.getColor(context, R.color.color_graph_red))
                                        graph_humid.setThumbColor(ContextCompat.getColor(context, R.color.color_graph_red))
                                        graph_humid.setTrackColor(ContextCompat.getColor(context, R.color.color_graph_red))
                                        graph_humid.setProgress(2f)
                                    }
                                }
                            }
                        }.apply { disposables.add(this) }


                    tv_co2_max.text = "0ppm"
                    tv_co2_min.text = "0ppm"

                    viewModel.sbjCo2CriticalMax
                        .subscribeOn(Schedulers.io())
                        .distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            tv_co2_max.text = it+"ppm"

                            if (viewModel.sbjSnsrType.value == SnsrType.CO2 && ll_sensor_cri.visibility == View.VISIBLE) {
                                tv_alarm_warn_max_value.setText(it)
                                tv_alarm_warn_max_value.setSelection(tv_alarm_warn_max_value.text.length)
                            }
                        }.apply { disposables.add(this) }


                    viewModel.sbjCo2CriticalMin
                        .subscribeOn(Schedulers.io())
                        .distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            tv_co2_min.text = it+"ppm"

                            if (viewModel.sbjSnsrType.value == SnsrType.CO2 && ll_sensor_cri.visibility == View.VISIBLE) {
                                tv_alarm_warn_min_value.setText(it)
                                tv_alarm_warn_min_value.setSelection(tv_alarm_warn_min_value.text.length)
                            }
                        }.apply { disposables.add(this) }



                    Observable.combineLatest(arrayOf(viewModel.sbjCo2CriticalMin, viewModel.sbjCo2, viewModel.sbjCo2CriticalMax),{
                        viewModel.isBetween(it[0].toString(), it[1].toString(), it[2].toString())
                    }).subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            context?.let {context ->
                                when(it) {

                                    PlantingDetailViewModel.IS_BETWEEN.LOWER ->  {
                                        graph_co2.setSecondTrackColor(ContextCompat.getColor(context, R.color.color_graph_yellow))
                                        graph_co2.setThumbColor(ContextCompat.getColor(context, R.color.color_graph_yellow))
                                        graph_co2.setTrackColor(ContextCompat.getColor(context, R.color.color_graph_yellow))
                                        graph_co2.setProgress(0f)
                                    }

                                    PlantingDetailViewModel.IS_BETWEEN.BETWEEN ->  {
                                        graph_co2.setSecondTrackColor(ContextCompat.getColor(context, R.color.color_graph_green))
                                        graph_co2.setThumbColor(ContextCompat.getColor(context, R.color.color_graph_green))
                                        graph_co2.setTrackColor(ContextCompat.getColor(context, R.color.color_graph_green))
                                        graph_co2.setProgress(1f)
                                    }

                                    PlantingDetailViewModel.IS_BETWEEN.UPPER ->  {
                                        graph_co2.setSecondTrackColor(ContextCompat.getColor(context, R.color.color_graph_red))
                                        graph_co2.setThumbColor(ContextCompat.getColor(context, R.color.color_graph_red))
                                        graph_co2.setTrackColor(ContextCompat.getColor(context, R.color.color_graph_red))
                                        graph_co2.setProgress(2f)
                                    }
                                }
                            }
                        }.apply { disposables.add(this) }


                    viewModel.sbjcropList
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            addCropList(it)
                        }.apply { disposables.add(this) }

                    viewModel.progress
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            loading.show(it)
                        }.apply { disposables.add(this) }

                    viewModel.sbjSnsrType
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            when (it) {
                                SnsrType.TEMPER -> {
                                    tv_alarm_warn_max_value.setText(viewModel.sbjTempCriticalMax.value ?: "0")
                                    tv_alarm_warn_min_value.setText(viewModel.sbjTempCriticalMin.value ?: "0")
                                }
                                SnsrType.HUMID -> {
                                    tv_alarm_warn_max_value.setText(viewModel.sbjHumidityCriticalMax.value ?: "0")
                                    tv_alarm_warn_min_value.setText(viewModel.sbjHumidityCriticalMin.value ?: "0")
                                }
                                SnsrType.CO2 -> {
                                    tv_alarm_warn_max_value.setText(viewModel.sbjCo2CriticalMax.value ?: "0")
                                    tv_alarm_warn_min_value.setText(viewModel.sbjCo2CriticalMin.value ?: "0")
                                }
                            }
                        }.apply { disposables.add(this) }



                    viewModel.sbjShowAlarmSensor
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            if (it) {
                                btn_alarm_sensor.visibility = View.VISIBLE
                            } else {
                                btn_alarm_sensor.visibility = View.INVISIBLE
                            }

                        }.apply { disposables.add(this) }

                    viewModel.fetchSnsrCriticalList()
                    viewModel.fetchCropList()
                    viewModel.fetchReloadControl()

                }
            }
        }
    }

    override fun onPause() {

        super.onPause()
    }

    override fun onDestroyView() {
        viewModel.stopTimer()
        super.onDestroyView()
    }

    private fun setupListView() {

        list_control.adapter?.let {
            return
        }
        val adapter = ControlAdapter(this, viewModel)
        list_control.apply {
            //                    this.layoutManager = LinearLayoutManager(it)
            this.adapter = adapter
        }

    }

    private fun setupList(list: List<ControlData>) {
        (list_control.adapter as ControlAdapter).run {
            this.setControlList(list)
        }
    }


    fun showAlertDialog() {
        if (context != null) {
            val items = resources.getStringArray(R.array.report_picker)
            val dialog: AlertDialog.Builder = AlertDialog.Builder(context!!)
            dialog.setTitle(R.string.plainting_detail_album)
            dialog.setItems(items, DialogInterface.OnClickListener { dialog, position ->
                when (position) {
                    0 -> {
                        selectImageInAlbum()
                    }
                    1 -> {
                        selectRemoveImage()
                    }
                }
            })
            dialog.setPositiveButton(R.string.picker_cancel, DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            val alert: AlertDialog = dialog.create()
            alert.show()
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.cl_container -> {
                tv_alarm_warn_min_value.hideKeyboard()
                tv_alarm_warn_min_value.clearFocus()

                tv_alarm_warn_max_value.hideKeyboard()
                tv_alarm_warn_max_value.clearFocus()
            }
            // 제어 버튼 클릭.
            R.id.btn_alarm_sensor -> {
                if(ll_sensor_cri.isVisible) {
                    setBtnChecked(btn_tag_temp)
                    viewModel.sbjSnsrType.onNext(SnsrType.TEMPER)

                    ll_sensor_cri.visibility = View.GONE
                    btn_alarm_sensor.text = getString(R.string.planting_btn_alarm)

                    tv_alarm_warn_min_value.hideKeyboard()
                    tv_alarm_warn_min_value.clearFocus()

                    tv_alarm_warn_max_value.hideKeyboard()
                    tv_alarm_warn_max_value.clearFocus()
                } else {
                    ll_sensor_cri.visibility = View.VISIBLE
                    btn_alarm_sensor.text = getString(R.string.planting_btn_alarm_close)


                    tv_alarm_warn_max_value.setText(viewModel.sbjTempCriticalMax.value ?: "0")
                    tv_alarm_warn_min_value.setText(viewModel.sbjTempCriticalMin.value ?: "0")

                }
            }
            R.id.btn_tag_temp -> {
                setBtnChecked(view)
                viewModel.sbjSnsrType.onNext(SnsrType.TEMPER)
            }
            R.id.btn_tag_humid -> {
                setBtnChecked(view)
                viewModel.sbjSnsrType.onNext(SnsrType.HUMID)
            }
            R.id.btn_tag_co2 -> {
                setBtnChecked(view)
                viewModel.sbjSnsrType.onNext(SnsrType.CO2)
            }
            R.id.btn_reg_picture -> {
//                showAlertDialog()
                selectImageInAlbum()
            }
            R.id.btn_cctv -> {
                val farmcctvId = viewModel.farmcctvId
                if (farmcctvId.isNotEmpty()) {
                    val intent = Intent(context, CCTVActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra(C.INTENT_URL_STRING, "")
                    intent.putExtra(C.INTENT_URL_CCTV_FARM_ID, farmcctvId)
                    startActivity(intent)
                } else {
                    Toast.makeText(context, getString(R.string.cctv_not_exist),Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btn_control_reload -> {
                viewModel.fetchReloadControl()
            }
            R.id.btn_apply_sensor -> {
                showSetSensorCritical()
            }
        }
    }


    fun selectImageInAlbum() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {

                TedRxBottomPicker.with(activity) //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                    .setTitle(R.string.plainting_detail_image)
                    .setSelectedUri(viewModel.selectedUri.value) //.showVideoMedia()
                    .setPeekHeight(1200)
                    .showCameraTile(true)
                    .show()
                    .subscribe(
                        { uri: Uri ->
                            viewModel.selectedUri.onNext(uri)
                            viewModel.selectedImageType()

                            view?.context?.let {
                                val mAlertDialog: AlertDialog
                                val builder: AlertDialog.Builder = AlertDialog.Builder(it)
                                builder.setCancelable(false)
                                builder.setMessage(getString(R.string.control_req_upload_image))
                                builder.setNegativeButton(it.getString(R.string.picker_cancel), { _, _ ->

                                })
                                builder.setPositiveButton(it.getString(R.string.alert_ok), { _, _ ->
                                    viewModel.fetchSendImage()
                                })
                                mAlertDialog = builder.create()
                                mAlertDialog.show()
                            }
//                                iv_image.visibility = View.VISIBLE
//                                mSelectedImagesContainer.setVisibility(View.GONE)
//                                requestManager
//                                    .load(uri)
//                                    .into(iv_image)
                        }
                    ) { obj: Throwable -> obj.printStackTrace() }
                    .apply { disposables.add(this) }


//                TedBottomPicker.with(activity) //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
//                    .setSelectedUri(viewModel.selectedUri.value) //.showVideoMedia()
//                    .setPeekHeight(1200)
//                    .show { uri: Uri ->
//                        Log.d(TAG, "uri: $uri")
//                        Log.d(TAG, "uri.getPath(): " + uri.path)
//                        viewModel.selectedUri.onNext(uri)
//                    }
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Toast.makeText(
                    activity,
                    R.string.permission_denied,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        checkPermission(permissionlistener)
    }

    fun selectVideoInAlbum() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                TedRxBottomPicker.with(activity) //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                    .setTitle(R.string.plainting_detail_video)
                    .setSelectedUri(viewModel.selectedUri.value) //.showVideoMedia()
                    .setPeekHeight(1200)
                    .showVideoMedia()
                    .show()
                    .subscribe({ uri: Uri ->
                        L.d( "uri: $uri")
                        L.d( "uri.getPath(): " + uri.path)
                        viewModel.selectedUri.onNext(uri)
                        viewModel.selectedVideoType()
                    }) { obj: Throwable -> obj.printStackTrace() }
                    .apply { disposables.add(this) }
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Toast.makeText(
                    activity,
                    deniedPermissions.toString() + " " + R.string.permission_denied,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        checkPermission(permissionlistener)
    }

    fun selectRemoveImage() {
        viewModel.selectedUri.onNext(Uri.EMPTY)
        viewModel.selectedNoneType()
    }

    private fun checkPermission(permissionlistener: PermissionListener) {
        TedPermission.with(context)
            .setPermissionListener(permissionlistener)
            .setDeniedMessage(R.string.permission_denied_alert)
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    private fun checkCameraPermission(permissionlistener: PermissionListener) {
        TedPermission.with(context)
            .setPermissionListener(permissionlistener)
            .setDeniedMessage(R.string.permission_denied_alert)
            .setPermissions(Manifest.permission.CAMERA)
            .check()
    }

//    fun showNumberPicker(view: View) {
//        showNumberPicker(view, true)
//    }
//
//    fun showNumberPicker(view: View, firstShow: Boolean){
//        val newFragment = NumberPickerDialog()
//        //Dialog에는 bundle을 이용해서 파라미터를 전달한다
//        val bundle = Bundle(6) // 파라미터는 전달할 데이터 개수
//
//        if (firstShow) {
//            if(view.id == R.id.tv_alarm_warn_max_value){
//                var defValue = tv_alarm_warn_max_value.text.toString().toInt()
//
//                bundle.putString("title", "최대치 선택") // key , value
//                bundle.putString("subtitle", "최대치 값을 선택해주세요.") // key , value
//                bundle.putInt("maxvalue", 50) // key , value
//                bundle.putInt("minvalue", 0) // key , value
//                bundle.putInt("step", 1) // key , value
//                bundle.putInt("defvalue", defValue) // key , value
//            } else {
//                var defValue = tv_alarm_warn_min_value.text.toString().toInt()
//                bundle.putString("title", "최소치 선택") // key , value
//                bundle.putString("subtitle", "최소치 값을 선택해주세요.") // key , value
//                bundle.putInt("maxvalue", 50) // key , value
//                bundle.putInt("minvalue", 0) // key , value
//                bundle.putInt("step", 1) // key , value
//                bundle.putInt("defvalue", defValue) // key , value
//            }
//        } else {
//            if(view.id != R.id.tv_alarm_warn_max_value){
//                var defValue = tv_alarm_warn_max_value.text.toString().toInt()
//
//                bundle.putString("title", "최대치 선택") // key , value
//                bundle.putString("subtitle", "최대치 값을 선택해주세요.") // key , value
//                bundle.putInt("maxvalue", 50) // key , value
//                bundle.putInt("minvalue", 0) // key , value
//                bundle.putInt("step", 1) // key , value
//                bundle.putInt("defvalue", defValue) // key , value
//            } else {
//                var defValue = tv_alarm_warn_min_value.text.toString().toInt()
//                bundle.putString("title", "최소치 선택") // key , value
//                bundle.putString("subtitle", "최소치 값을 선택해주세요.") // key , value
//                bundle.putInt("maxvalue", 50) // key , value
//                bundle.putInt("minvalue", 0) // key , value
//                bundle.putInt("step", 1) // key , value
//                bundle.putInt("defvalue", defValue) // key , value
//            }
//        }
//
//
//        newFragment.arguments = bundle
//        //class 자신을 Listener로 설정한다
//
//        newFragment.valueChangeListener = NumberPicker.OnValueChangeListener { picker, i1, i2 ->
//            val setting_value = 0 + picker.getValue()*1
//            L.d("setting_value : ${setting_value} | ${i1} | ${i2}")
//
//            if (firstShow) {
//                when (view.id) {
//                    R.id.tv_alarm_warn_max_value -> {
//                        when (viewModel.sbjSnsrType.value) {
//                            SnsrType.TEMPER -> {
//                                viewModel.sbjTempCriticalMax.onNext(setting_value.toString())
//                            }
//                            SnsrType.HUMID -> {
//                                viewModel.sbjHumidityCriticalMax.onNext(setting_value.toString())
//                            }
//                            SnsrType.CO2 -> {
//                                viewModel.sbjCo2CriticalMax.onNext(setting_value.toString())
//                            }
//                        }
//                        viewModel.beforeMaxVal = setting_value.toString()
//                    }
//                    R.id.tv_alarm_warn_min_value -> {
//                        when (viewModel.sbjSnsrType.value) {
//                            SnsrType.TEMPER -> {
//                                viewModel.sbjTempCriticalMin.onNext(setting_value.toString())
//                            }
//                            SnsrType.HUMID -> {
//                                viewModel.sbjHumidityCriticalMin.onNext(setting_value.toString())
//                            }
//                            SnsrType.CO2 -> {
//                                viewModel.sbjCo2CriticalMin.onNext(setting_value.toString())
//                            }
//                        }
//                        viewModel.beforeMinVal = setting_value.toString()
//                    }
//                }
//                showNumberPicker(view, false)
//            } else {
//                when (view.id) {
//                    R.id.tv_alarm_warn_max_value -> {
//                        when (viewModel.sbjSnsrType.value) {
//                            SnsrType.TEMPER -> {
//                                viewModel.sbjTempCriticalMin.onNext(setting_value.toString())
//                            }
//                            SnsrType.HUMID -> {
//                                viewModel.sbjHumidityCriticalMin.onNext(setting_value.toString())
//                            }
//                            SnsrType.CO2 -> {
//                                viewModel.sbjCo2CriticalMin.onNext(setting_value.toString())
//                            }
//                        }
//                        viewModel.beforeMinVal = setting_value.toString()
//                    }
//                    R.id.tv_alarm_warn_min_value -> {
//                        when (viewModel.sbjSnsrType.value) {
//                            SnsrType.TEMPER -> {
//                                viewModel.sbjTempCriticalMax.onNext(setting_value.toString())
//                            }
//                            SnsrType.HUMID -> {
//                                viewModel.sbjHumidityCriticalMax.onNext(setting_value.toString())
//                            }
//                            SnsrType.CO2 -> {
//                                viewModel.sbjCo2CriticalMax.onNext(setting_value.toString())
//                            }
//                        }
//                        viewModel.beforeMaxVal = setting_value.toString()
//                    }
//                }
//
//                showSetSensorCritical()
//            }
//        }
//        fragmentManager?.let {
//            newFragment.show(it, "number picker")
//        }
//    }

    fun startAnim(view: View, color1: Int, color2: Int) {
        val anim = ValueAnimator()
        anim.setIntValues(color1, color2)
        anim.setEvaluator(android.animation.ArgbEvaluator())
        anim.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
                view.setBackgroundColor(valueAnimator.getAnimatedValue() as Int)
            }
        })
        anim.setDuration(300)
        anim.start()
    }


    fun setBtnChecked(view: View) {
        val viewArr = arrayOf(btn_tag_temp, btn_tag_humid, btn_tag_co2)
        for (v in viewArr) {
            (v as? CheckedTextView)?.run {
                L.d("why???? 2: ${view.id} | ${v.id}")
                this.isChecked = view.id == v.id
            }
        }
    }

    fun showSetSensorCritical(){
        view?.context?.let {
            val mAlertDialog: AlertDialog
            val builder: AlertDialog.Builder = AlertDialog.Builder(it)
            builder.setCancelable(false)
            builder.setMessage(getString(R.string.control_sensor_warn_change_msg))
            builder.setNegativeButton(it.getString(R.string.picker_cancel), { _, _ ->
            })
            builder.setPositiveButton(it.getString(R.string.alert_ok), { _, _ ->
                viewModel.fetchModifySnsr()
            })
            mAlertDialog = builder.create()
            mAlertDialog.show()
        }
    }

    fun addCropList(list: List<CropData>) {
        ll_content.removeAllViews()

        if (list.isEmpty()) {
            ll_state.visibility = View.GONE
        } else {
            ll_state.visibility = View.VISIBLE
        }

        for (cItem in list) {
            val itemView = LayoutInflater.from(activity).inflate(
                R.layout.item_crop_list, null
            )

            val cropItem = itemView.findViewById<LinearLayout>(R.id.ll_crop_item)

            cropItem.tv_name_content.text = cItem.cropNm
            cropItem.tv_date_start_content.text = cItem.regDate

            if (cItem.regDate.isNotEmpty()) {

                val date = calDateBetweenAandB(todayDateString(), cItem.regDate)
                cropItem.tv_date_count_content.text = "${date}일"
            } else {
                cropItem.tv_date_count_content.text = "0일"
            }



//            val tvName = itemView.findViewById<TextView>(R.id.tv_name_content)
//            val tvDateStart = itemView.findViewById<TextView>(R.id.tv_date_start_content)
//            val tvDateCount = itemView.findViewById<TextView>(R.id.tv_date_count_content)

            (cropItem.parent as? ViewGroup)?.removeView(cropItem)
            ll_content.addView(cropItem)
        }
    }

    fun todayDateString(): String {
        val format = SimpleDateFormat("yyyy-mm-dd")
        val cal = Calendar.getInstance()

        return format.format(cal.time)
    }

    fun calDateBetweenAandB(date1: String, date2: String): Long {
        try { // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
            val format = SimpleDateFormat("yyyy-mm-dd")
            // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.
            val FirstDate: Date = format.parse(date1)
            val SecondDate: Date = format.parse(date2)
            // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
// 연산결과 -950400000. long type 으로 return 된다.
            val calDate: Long = FirstDate.getTime() - SecondDate.getTime()
            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
// 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            var calDateDays = calDate / (24 * 60 * 60 * 1000)
            calDateDays = Math.abs(calDateDays)
            println("두 날짜의 날짜 차이: $calDateDays")

            return calDateDays
        } catch (e: ParseException) { // 예외 처리
        }
        return 0
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }


    override fun onBackPressed() {
//        super.onBackPressed()
        L.d("test3333##########3333##")
        backefreshList()
    }

    fun backefreshList() {
        val act =(this.activity as? PlantingActivity)
        act?.reload = viewModel.isChanged

        navController.popBackStack()
    }

}