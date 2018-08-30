package com.android.nanodegree.kt.moviesapp.util

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
class DimensionsUtil private constructor() {
    companion object {

        private const val TABLET_STARTING_WIDTH_IN_DP = 600

        private fun getScreenWidthInDp(context: Context?) = context?.let {
            val displayMetrics = DisplayMetrics()
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                    .defaultDisplay.getMetrics(displayMetrics)
            Math.round(displayMetrics.widthPixels / displayMetrics.density)
        } ?: throw IllegalArgumentException("Context cannot be null")

        fun isTablet(context: Context?) =
                getScreenWidthInDp(context) >= TABLET_STARTING_WIDTH_IN_DP

        fun getGridSpanCount(context: Context?, gridItemWidth: Int) =
                getScreenWidthInDp(context) / gridItemWidth
    }

}