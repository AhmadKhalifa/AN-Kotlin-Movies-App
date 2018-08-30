package com.android.nanodegree.kt.moviesapp.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
class DateFormatter private constructor() {
    companion object {

        /**
         * Takes a date in dd-MM-yyyy format and returns the same date with MMM d, yyyy format.
         */
        @SuppressLint("SimpleDateFormat")
        fun format(oldFormat: String?) = oldFormat?.let {
            try {
                SimpleDateFormat("MMM d, yyyy")
                        .format(SimpleDateFormat("yyyy-MM-dd").parse(it).toString())
            } catch (exception: Exception) {
                exception.printStackTrace()
                oldFormat
            }
        }
    }

}