package com.android.nanodegree.kt.moviesapp.data.repository.movies.local

import com.android.nanodegree.kt.moviesapp.data.repository.BaseLocalRepository
import com.android.nanodegree.kt.moviesapp.data.repository.movies.IMoviesRepository
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.data.model.QueryType

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
abstract class BaseLocalMoviesRepository : BaseLocalRepository(), IMoviesRepository {

    @Throws(Exception::class)
    abstract fun cacheMovies(queryType: QueryType, movies: List<Movie>)

    @Throws(Exception::class)
    abstract fun isFavoriteMovie(movieId: String): Boolean

    @Throws(Exception::class)
    abstract fun addMovieToFavorites(movie: Movie)

    @Throws(Exception::class)
    abstract fun removeMovieFromFavorites(movieId: String)

    @Throws(Exception::class)
    abstract fun loadFavoriteMoviesIds(): HashSet<Long>
}