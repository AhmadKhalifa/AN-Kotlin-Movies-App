package com.android.nanodegree.kt.moviesapp.data.repository.movies.local

import android.content.Context
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.data.model.QueryType
import com.android.nanodegree.kt.moviesapp.data.storage.room.MoviesDao
import com.android.nanodegree.kt.moviesapp.data.storage.room.MoviesDatabase

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
class RoomLocalMoviesRepository(private val applicationContext: Context) :
        BaseLocalMoviesRepository() {

    private var _moviesDao: MoviesDao? = null

    private val moviesDao: MoviesDao
        get() {
            _moviesDao = _moviesDao ?: MoviesDatabase.getInstance(applicationContext).moviesDao()
            return if (_moviesDao != null) _moviesDao as MoviesDao
            else throw IllegalStateException(
                    "MoviesDatabase.getInstance(applicationContext).moviesDao() " +
                            "implementation returns null!"
            )
        }

    override fun loadMovies(queryType: QueryType) = when (queryType) {
        QueryType.MOST_POPULAR -> moviesDao.getMostPopularMovies()
        QueryType.TOP_RATED -> moviesDao.getTopRatedMovies()
        QueryType.FAVORITES -> moviesDao.getFavoriteMovies()
    }

    override fun cacheMovies(queryType: QueryType, movies: List<Movie>) {
        val dbMoviesMap = HashMap<Long, Movie>().also { map ->
            moviesDao.getMoviesByIds(movies.map { it.id }.toLongArray()).value!!
                    .forEach { movie -> map.put(movie.id, movie) }
        }
        moviesDao.addOrUpdateMovies(*Array(
                movies.size,
                { index -> getMovieWithDatabaseFlags(movies, index, queryType, dbMoviesMap) }
        ))
    }

    private fun getMovieWithDatabaseFlags(movies: List<Movie>,
                                          index: Int,
                                          queryType: QueryType,
                                          dbMoviesMap: HashMap<Long, Movie>): Movie {
        val movie: Movie = movies[index]
        if (dbMoviesMap.containsKey(movies[index].id) &&
                dbMoviesMap[movies[index].id] != null &&
                dbMoviesMap[movies[index].id]?.databaseStatus != null) {
            movie.databaseStatus = dbMoviesMap[movies[index].id]!!.databaseStatus
        }
        return if (queryType == QueryType.MOST_POPULAR) setMostPopularFlag(movie, true)
                else setTopRatedFlag(movie, true)
    }

    override fun isFavoriteMovie(movieId: String) = MoviesDatabase.getInstance(applicationContext)
            .moviesDao().getMovie(movieId).value?.databaseStatus?.isFavorite ?: false

    override fun addMovieToFavorites(movie: Movie) {
        val dbMovie = moviesDao.getMovie(movie.id.toString()).value
        dbMovie?.let {
            movie.databaseStatus = it.databaseStatus
        }
        moviesDao.addOrUpdateMovie(setFavoritesFlag(movie, true))
    }

    override fun removeMovieFromFavorites(movieId: String) {
        val dbMovie = moviesDao.getMovie(movieId).value
        dbMovie?.let {
            it.databaseStatus.isFavorite = false
            moviesDao.addOrUpdateMovie(setFavoritesFlag(it, false))
        } ?: throw IllegalStateException("Movie isn't already in database!")
    }

    override fun loadFavoriteMoviesIds() =
            moviesDao.getFavoriteMovies().value?.map { it.id }?.toHashSet()!!

    private fun setMostPopularFlag(movie: Movie, flag: Boolean) = movie.also {
        it.databaseStatus.isMostPopular = flag
    }

    private fun setTopRatedFlag(movie: Movie, flag: Boolean) = movie.also {
        it.databaseStatus.isTopRated = flag
    }

    private fun setFavoritesFlag(movie: Movie, flag: Boolean) = movie.also {
        it.databaseStatus.isFavorite = flag
    }
}
