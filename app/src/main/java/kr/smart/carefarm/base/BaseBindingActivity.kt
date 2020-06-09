package kr.smart.carefarm.base

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

open class BaseBindingActivity<B : ViewDataBinding>(
    private val layoutId: Int
) : BaseActivity() {
    protected lateinit var binding: B


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.setLifecycleOwner { this.lifecycle }
    }
}