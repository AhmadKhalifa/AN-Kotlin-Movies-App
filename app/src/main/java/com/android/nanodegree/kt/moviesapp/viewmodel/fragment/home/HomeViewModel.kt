package com.android.nanodegree.kt.moviesapp.viewmodel.fragment.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import com.android.nanodegree.kt.moviesapp.data.repository.movies.IMoviesRepository
import com.android.nanodegree.kt.moviesapp.data.repository.movies.local.BaseLocalMoviesRepository
import com.android.nanodegree.kt.moviesapp.data.repository.movies.remote.BaseRemoteMoviesRepository
import com.android.nanodegree.kt.moviesapp.exception.NoInternetConnectionException
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.data.model.QueryType
import com.android.nanodegree.kt.moviesapp.data.repository.movies.local.RoomLocalMoviesRepository
import com.android.nanodegree.kt.moviesapp.viewmodel.BaseRxViewModel
import com.android.nanodegree.kt.moviesapp.viewmodel.Error
import com.android.nanodegree.kt.moviesapp.viewmodel.Event
import com.android.nanodegree.kt.moviesapp.ui.fragment.HomeFragment

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
class HomeViewModel : BaseRxViewModel(), IHomeViewModel {

    val queryType = MutableLiveData<QueryType?>()

    val mostPopularMovies: MutableLiveData<Pair<Boolean, List<Movie>?>> = MutableLiveData()
    val topRatedMovies: MutableLiveData<Pair<Boolean, List<Movie>?>> = MutableLiveData()
    val favoriteMovies: MutableLiveData<List<Movie>?> = MutableLiveData()

    val favoriteMoviesIdsSet: MutableLiveData<HashSet<Long>?> = MutableLiveData()

    private var localRepository: BaseLocalMoviesRepository? = null
    private var remoteRepository: BaseRemoteMoviesRepository? = null

    companion object {

        @JvmStatic
        fun getInstance(homeFragment: HomeFragment,
                        localMoviesRepository: BaseLocalMoviesRepository,
                        remoteMoviesRepository: BaseRemoteMoviesRepository): HomeViewModel {
            return ViewModelProviders.of(homeFragment).get(HomeViewModel::class.java).also {
                if (it.localRepository == null)
                    it.localRepository = localMoviesRepository
                if (it.remoteRepository == null)
                    it.remoteRepository = remoteMoviesRepository
            }
        }
    }

    init {
        loadFavoriteMoviesIds()
    }

    fun getMoviesList(queryType: QueryType?): List<Movie>? = queryType?.let {
        when (it) {
            QueryType.MOST_POPULAR -> mostPopularMovies.value?.second
            QueryType.TOP_RATED -> topRatedMovies.value?.second
            QueryType.FAVORITES -> favoriteMovies.value
        }
    } ?: throw IllegalArgumentException("Query type cannot be null!")

    override fun loadMovies(queryType: QueryType?) = loadMovies(queryType, false)

    private fun loadMoviesLocally(queryType: QueryType?) = loadMovies(queryType, localRepository)

    private fun loadMoviesRemotely(queryType: QueryType?) = loadMovies(queryType, remoteRepository)

    override fun loadMovies(queryType: QueryType?, loadRemotely: Boolean) = if (loadRemotely)
        loadMoviesRemotely(queryType)
    else
        loadMoviesLocally(queryType)

    private fun loadMovies(queryType: QueryType?, moviesRepository: IMoviesRepository?) {
        if (queryType == QueryType.FAVORITES &&
                moviesRepository !is BaseLocalMoviesRepository) {
            throw UnsupportedOperationException(
                    "Favorite movies are only stored in a local repository"
            )
        }
        val isRequestingRemotely = moviesRepository is BaseRemoteMoviesRepository
        if (queryType != null && moviesRepository != null) {
            performAsync(
                    action = {
                        moviesRepository.loadMovies(queryType)
                    },
                    onSuccess = {
                        it?.let { movies ->
                            if (movies.value!!.isEmpty() && queryType == QueryType.FAVORITES ||
                                    !movies.value!!.isEmpty()) {
                                when (queryType) {
                                    QueryType.MOST_POPULAR -> {
                                        mostPopularMovies.value =
                                                Pair(isRequestingRemotely, movies.value)
                                    }
                                    QueryType.TOP_RATED -> {
                                        topRatedMovies.value =
                                                Pair(isRequestingRemotely, movies.value)
                                    }
                                    QueryType.FAVORITES -> {
                                        favoriteMovies.value = movies.value
                                    }
                                }
                                if (queryType != QueryType.FAVORITES)
                                    cacheMovies(queryType, movies.value!!)
                            } else {
                                loadMoviesRemotely(queryType)
                            }
                        } ?: notify(Error.ERROR_LOADING_MOVIES)
                    },
                    onFailure = { error ->
                        if (isRequestingRemotely) {
                            notify(
                                    if (error is NoInternetConnectionException)
                                        Error.NO_INTERNET_CONNECTION
                                    else
                                        Error.NON_STABLE_CONNECTION
                            )
                            notify(Error.ERROR_LOADING_MOVIES)
                        } else {
                            if (queryType == QueryType.FAVORITES) {
                                notify(Error.ERROR_LOADING_FAVORITE_MOVIES)
                            } else {
                                loadMoviesRemotely(queryType)
                            }
                        }
                    }
            )
        }
    }

    private fun cacheMovies(queryType: QueryType, movies: List<Movie>) = performAsync {
        localRepository?.cacheMovies(queryType, movies)
    }

    fun loadFavoriteMoviesIds() = performAsync(
            action = {
                localRepository?.loadFavoriteMoviesIds()
            },
            onSuccess = { favoritesIdsSet ->
                favoritesIdsSet?.let {
                    favoriteMoviesIdsSet.value = favoritesIdsSet
                } ?: throw IllegalStateException("Error loading favorite movies Ids")
            },
            onFailure = {
                throw IllegalStateException("Error loading favorite movies Ids", it)
            }
    )

    override fun addToFavorites(movie: Movie?) = movie?.let {
        performAsync(
                action = {
                    localRepository?.addMovieToFavorites(movie)
                },
                onSuccess = {
                    addToFavoritesSet(movie)
                    notify(Event.ADDED_TO_FAVORITES)
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
                    removeFromFavoritesSet(movie)
                    notify(Event.REMOVED_FROM_FAVORITES)
                },
                onFailure = {
                    notify(Error.ERROR_REMOVING_MOVIE_FROM_FAVORITES)
                }
        )
    }!!

    private fun addToFavoritesSet(movie: Movie) {
        var idsSet: HashSet<Long>? = favoriteMoviesIdsSet.value
        if (idsSet == null) {
            idsSet = HashSet()
        }
        idsSet.add(movie.id)
        favoriteMoviesIdsSet.value = idsSet
        favoriteMoviesIdsSet.value
        if (localRepository !is RoomLocalMoviesRepository) {
            favoriteMovies.value?.let {
                favoriteMovies.value =
                        arrayListOf(*favoriteMovies.value?.toTypedArray()!!, movie)
            }
            favoriteMoviesIdsSet.value =
                    hashSetOf(*favoriteMoviesIdsSet.value?.toTypedArray()!!, movie.id)
        }
    }

    private fun removeFromFavoritesSet(movie: Movie) = favoriteMoviesIdsSet.value?.let {
        it.remove(movie.id)
        favoriteMoviesIdsSet.value = it
        if (localRepository !is RoomLocalMoviesRepository) {
            favoriteMovies.value?.let {
                favoriteMovies.value =
                        arrayListOf(*favoriteMovies.value?.toTypedArray()!!, movie)
            }
            favoriteMoviesIdsSet.value =
                    hashSetOf(*favoriteMoviesIdsSet.value?.toTypedArray()!!, movie.id)
        }
    }

    fun isNetworkAvailable() = remoteRepository?.isNetworkAvailable() ?: false
}
