package com.android.nanodegree.kt.moviesapp.viewmodel.fragment.details

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import com.android.nanodegree.kt.moviesapp.data.repository.movies.local.BaseLocalMoviesRepository
import com.android.nanodegree.kt.moviesapp.data.repository.movies.remote.BaseRemoteMoviesRepository
import com.android.nanodegree.kt.moviesapp.exception.NoInternetConnectionException
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.viewmodel.BaseRxViewModel
import com.android.nanodegree.kt.moviesapp.viewmodel.Error
import com.android.nanodegree.kt.moviesapp.viewmodel.Event
import com.android.nanodegree.kt.moviesapp.ui.fragment.MovieDetailsFragment

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
class MovieDetailsViewModel : BaseRxViewModel(), IMovieDetailsViewModel {

    val movie = MutableLiveData<Movie>()
    val isFavorite = MutableLiveData<Boolean>()
    val trailers = MutableLiveData<List<Movie.Trailer>>()
    val reviews = MutableLiveData<List<Movie.Review>>()

    var localRepository: BaseLocalMoviesRepository? = null
    var remoteRepository: BaseRemoteMoviesRepository? = null

    companion object {
        @JvmStatic
        fun getInstance(movieDetailsFragment: MovieDetailsFragment,
                        localMoviesRepository: BaseLocalMoviesRepository,
                        remoteMoviesRepository: BaseRemoteMoviesRepository
        ): MovieDetailsViewModel = ViewModelProviders.of(movieDetailsFragment)
                .get(MovieDetailsViewModel::class.java).also {
            if (it.localRepository == null)
                it.localRepository = localMoviesRepository
            if (it.remoteRepository == null)
                it.remoteRepository = remoteMoviesRepository
        }
    }

    override fun loadMoviesTrailers(movieId: String?): Unit = movieId?.let {
        performAsync(
                action = {
                    remoteRepository?.loadMovieTrailers(movieId)
                },
                onSuccess = { trailers ->
                    trailers?.let { this.trailers.value = trailers }
                            ?: throw IllegalStateException()
                },
                onFailure = { error ->
                    if (error is NoInternetConnectionException)
                        notify(Error.NO_INTERNET_CONNECTION)
                    else
                        notify(Error.ERROR_LOADING_MOVIE_REVIEWS)
                }
        )
    }!!

    override fun loadMovieReviews(movieId: String?): Unit = movieId?.let {
        performAsync(
                action = {
                    remoteRepository?.loadMoviesReviews(movieId)
                },
                onSuccess = { reviews ->
                    reviews?.let { this.reviews.value = reviews }
                            ?: throw IllegalStateException()
                },
                onFailure = { error ->
                    if (error is NoInternetConnectionException)
                        notify(Error.NO_INTERNET_CONNECTION)
                    else
                        notify(Error.ERROR_LOADING_MOVIE_REVIEWS)
                }
        )
    }!!

    override fun isFavoriteMovie(movieId: String?): Unit = movieId?.let {
        performAsync(
                action = {
                    localRepository?.isFavoriteMovie(movieId)
                },
                onSuccess = { isFavorite ->
                    isFavorite?.let { this.isFavorite.value = isFavorite }
                            ?: throw IllegalStateException()
                },
                onFailure = {
                    notify(Error.ERROR_GETTING_MOVIE_FAVORITE_STATE)
                }
        )
    }!!

    override fun addToFavorites(movie: Movie?): Unit = movie?.let {
        performAsync(
                action = {
                    localRepository?.addMovieToFavorites(movie)
                },
                onSuccess = {
                    notify(Event.ADDED_TO_FAVORITES)
                    isFavorite
                },
                onFailure = {
                    notify(Error.ERROR_ADDING_MOVIE_TO_FAVORITES)
                }
        )
    }!!

    override fun removeFromFavorites(movie: Movie?): Unit = movie?.let {
        performAsync(
                action = {
                    localRepository?.removeMovieFromFavorites(movie.id.toString())
                },
                onSuccess = {
                    notify(Event.REMOVED_FROM_FAVORITES)
                },
                onFailure = {
                    notify(Error.ERROR_REMOVING_MOVIE_FROM_FAVORITES)
                }
        )
    }!!
}
