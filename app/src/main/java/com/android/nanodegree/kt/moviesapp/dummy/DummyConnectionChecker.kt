package com.android.nanodegree.kt.moviesapp.dummy

import com.android.nanodegree.kt.moviesapp.util.BaseConnectionChecker

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
class DummyConnectionChecker(private val isConnected: Boolean) : BaseConnectionChecker {

    override fun isNetworkAvailable() = isConnected
}