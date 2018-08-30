package com.android.nanodegree.kt.moviesapp.viewmodel.activity.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import com.android.nanodegree.kt.moviesapp.data.model.QueryType
import com.android.nanodegree.kt.moviesapp.ui.activity.HomeActivity
import com.android.nanodegree.kt.moviesapp.viewmodel.BaseRxViewModel

/**
 * Created by Khalifa on 27/08/2018.
 *
 */
class HomeActivityViewModel : BaseRxViewModel() {

    companion object {
        @JvmStatic
        fun getInstance(homeActivity: HomeActivity): HomeActivityViewModel =
                ViewModelProviders
                        .of(homeActivity)
                        .get(HomeActivityViewModel::class.java)
    }

    val queryType = MutableLiveData<QueryType>()
}