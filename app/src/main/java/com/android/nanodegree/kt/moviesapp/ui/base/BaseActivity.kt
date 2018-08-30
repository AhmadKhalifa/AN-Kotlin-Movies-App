package com.android.nanodegree.kt.moviesapp.ui.base

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import com.android.nanodegree.kt.moviesapp.viewmodel.BaseRxViewModel
import com.android.nanodegree.kt.moviesapp.viewmodel.BaseViewModelOwner

/**
 * Created by Khalifa on 12/08/2018.
 *
 */

@SuppressLint("Registered")
abstract class BaseActivity<out VM : BaseRxViewModel> :
        AppCompatActivity(),
        BaseViewModelOwner<VM> {

    private var _viewModel: VM? = null

    protected val viewModel: VM
        get() {
            _viewModel = _viewModel ?: getViewModelInstance()
            return if (_viewModel != null) _viewModel as VM
            else throw IllegalStateException("getViewModelInstance() implementation returns null!")
        }

    override fun onResume() {
        super.onResume()
        registerEventHandlerSubscribers(this, viewModel)
        registerLiveDataObservers()
    }

    override fun onDestroy() {
        viewModel.clearDisposables()
        super.onDestroy()
    }
}
