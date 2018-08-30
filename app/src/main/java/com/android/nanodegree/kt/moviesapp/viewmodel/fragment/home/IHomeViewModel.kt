package com.android.nanodegree.kt.moviesapp.viewmodel.fragment.home

import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.data.model.QueryType

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
interface IHomeViewModel {

    fun loadMovies(queryType: QueryType?)

    fun loadMovies(queryType: QueryType?, loadRemotely: Boolean)

    fun addToFavorites(movie: Movie?)

    fun removeFromFavorites(movie: Movie?)
}

