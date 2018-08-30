package com.android.nanodegree.kt.moviesapp.data.repository.movies

import android.arch.lifecycle.LiveData
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.data.model.QueryType

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
interface IMoviesRepository {

    fun loadMovies(queryType: QueryType): LiveData<List<Movie>>
}