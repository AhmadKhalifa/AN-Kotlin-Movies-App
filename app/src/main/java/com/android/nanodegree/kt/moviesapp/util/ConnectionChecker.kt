package com.android.nanodegree.kt.moviesapp.util

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
class ConnectionChecker(private val applicationContext: Context?) : BaseConnectionChecker {

    override fun isNetworkAvailable() = applicationContext?.let {
        (it.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo
                .isConnectedOrConnecting
    } ?: false
}