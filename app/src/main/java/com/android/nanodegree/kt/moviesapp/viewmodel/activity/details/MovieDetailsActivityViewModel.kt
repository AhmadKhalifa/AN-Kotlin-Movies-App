package com.android.nanodegree.kt.moviesapp.viewmodel.activity.details

import android.arch.lifecycle.ViewModelProviders
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.ui.activity.MovieDetailsActivity
import com.android.nanodegree.kt.moviesapp.viewmodel.BaseRxViewModel

/**
 * Created by Khalifa on 15/08/2018.
 *
 */
class MovieDetailsActivityViewModel : BaseRxViewModel() {

    companion object {
        @JvmStatic
        fun getInstance(movieDetailsActivity: MovieDetailsActivity): MovieDetailsActivityViewModel =
                ViewModelProviders
                        .of(movieDetailsActivity)
                        .get(MovieDetailsActivityViewModel::class.java)
    }

    var movie: Movie? = null
}