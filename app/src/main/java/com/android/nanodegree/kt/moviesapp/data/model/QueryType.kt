package com.android.nanodegree.kt.moviesapp.data.model

import android.support.annotation.StringRes
import com.android.nanodegree.kt.moviesapp.R

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
enum class QueryType(private val value: String, @StringRes val resourceId: Int) {

    MOST_POPULAR("popular", R.string.most_popular),
    TOP_RATED("top_rated", R.string.top_rated),
    FAVORITES("favorites", R.string.favorites);

    override fun toString() = value
}