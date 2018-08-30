package com.android.nanodegree.kt.moviesapp.ui.base

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.View
import com.android.nanodegree.kt.moviesapp.viewmodel.BaseRxViewModel
import com.android.nanodegree.kt.moviesapp.viewmodel.BaseViewModelOwner

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
abstract class BaseFragment<out VM : BaseRxViewModel> : Fragment(), BaseViewModelOwner<VM> {

    private var _viewModel: VM? = null
    private var _rootView: View? = null
    private var snackBar: Snackbar? = null

    protected val rootView: View
        get() {
            return if (_rootView != null) _rootView as View
            else throw IllegalStateException(
                        "Root view cannot be null. " +
                                "It MUST hold the root view created in the onViewCreated method"
            )
        }

    protected val viewModel: VM
        get() {
            _viewModel = _viewModel ?: getViewModelInstance()
            return if (_viewModel != null) _viewModel as VM
            else throw IllegalStateException("getViewModelInstance() implementation returns null!")
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _rootView = view
        registerEventHandlerSubscribers(this, viewModel)
        registerLiveDataObservers()
        super.onViewCreated(view, savedInstanceState)
    }

    protected fun showMessage(messageResId: Int) =
            showMessage(context?.resources?.getString(messageResId))

    private fun showMessage(message: String?): Unit = message?.let {
        snackBar?.dismiss()
        snackBar = Snackbar.make(rootView, it, Snackbar.LENGTH_SHORT)
        snackBar?.show()
    }!!

    protected fun showMessage(newSnackBar: Snackbar?) = newSnackBar?.let {
        snackBar?.dismiss()
        snackBar = it
        snackBar?.show()
    }

    protected fun dismissMessagesIfAny() = snackBar?.dismiss()

    override fun onDestroy() {
        viewModel.clearDisposables()
        super.onDestroy()
    }
}