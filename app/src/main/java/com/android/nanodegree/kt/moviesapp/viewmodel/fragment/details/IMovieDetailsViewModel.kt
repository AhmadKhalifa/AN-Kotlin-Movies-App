package com.android.nanodegree.kt.moviesapp.viewmodel.fragment.details

import com.android.nanodegree.kt.moviesapp.data.model.Movie

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
interface IMovieDetailsViewModel {

    fun loadMoviesTrailers(movieId: String?)

    fun loadMovieReviews(movieId: String?)

    fun isFavoriteMovie(movieId: String?)

    fun addToFavorites(movie: Movie?)

    fun removeFromFavorites(movie: Movie?)
}