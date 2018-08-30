package com.android.nanodegree.kt.moviesapp.data.repository.movies.remote

import com.android.nanodegree.kt.moviesapp.data.repository.BaseRemoteRepository
import com.android.nanodegree.kt.moviesapp.data.repository.movies.IMoviesRepository
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.util.BaseConnectionChecker

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
abstract class BaseRemoteMoviesRepository(
        connectionChecker: BaseConnectionChecker,
        val apiKey: String?
) : BaseRemoteRepository(connectionChecker, BASE_URL), IMoviesRepository {

    companion object {

        protected const val BASE_URL = "http://api.themoviedb.org"
    }

    @Throws(Exception::class)
    abstract fun loadMovieTrailers(movieId: String): List<Movie.Trailer>?

    @Throws(Exception::class)
    abstract fun loadMoviesReviews(movieId: String): List<Movie.Review>?
}