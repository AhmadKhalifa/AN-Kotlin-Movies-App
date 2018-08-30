package com.android.nanodegree.kt.moviesapp.data.repository.movies.local

import android.arch.lifecycle.MutableLiveData
import android.database.Cursor
import android.database.CursorWrapper
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.data.model.QueryType
import com.android.nanodegree.kt.moviesapp.data.storage.contract.MoviesDataBaseContracts
import com.android.nanodegree.kt.moviesapp.data.storage.sqlite.handler.MoviesSQLiteDatabaseHandler

/**
 * Created by Khalifa on 26/08/2018.
 *
 */
class SQLiteLocalMoviesRepository(private val storageHandler: MoviesSQLiteDatabaseHandler) :
        BaseLocalMoviesRepository() {

    override fun loadMovies(queryType: QueryType) = MutableLiveData<List<Movie>>().also { data ->
        val cursorWrapper = MoviesCursorWrapper(storageHandler.getMoviesCursor(queryType))
        val moviesList = ArrayList<Movie>()
        try {
            if (cursorWrapper.moveToFirst()) {
                do {
                    moviesList.add(cursorWrapper.movie)
                } while (cursorWrapper.moveToNext())
            }
        } finally {
            cursorWrapper.close()
        }
        data.value = moviesList
//        cursorWrapper.use {
//            data.value = ArrayList()
//            if (it.moveToFirst())
//                do {
//                    (data.value as ArrayList<Movie>).add(it.movie)
//                } while (it.moveToNext())
//        }
    }

    override fun cacheMovies(queryType: QueryType, movies: List<Movie>) = with(storageHandler) {
        clearTable(queryType)
        cacheMovies(queryType, MoviesDataBaseContracts.getMoviesContentValuesArray(movies))
    }

    override fun isFavoriteMovie(movieId: String) =
            storageHandler.isFavoriteMovie(movieId)

    override fun addMovieToFavorites(movie: Movie) =
            storageHandler.addMovieToFavorites(MoviesDataBaseContracts.getMovieContentValues(movie))

    override fun removeMovieFromFavorites(movieId: String) =
            storageHandler.removeMovieFromFavorites(movieId)

    override fun loadFavoriteMoviesIds() = HashSet<Long>().also { set ->
        class MoviesIdsCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {

            val movieId: String
                get() = getString(getColumnIndex(MoviesDataBaseContracts.BaseMoviesEntry.COL_ID))
        }

        val cursorWrapper = MoviesIdsCursorWrapper(storageHandler.loadFavoriteMoviesIds())
        try {
            if (cursorWrapper.moveToFirst()) {
                do {
                    set.add(cursorWrapper.movieId.toLong())
                } while (cursorWrapper.moveToNext())
            }
        } finally {
            cursorWrapper.close()
        }
//        MoviesIdsCursorWrapper(storageHandler.loadFavoriteMoviesIds()).use {
//            if (it.moveToFirst()) {
//                do {
//                    set.add(it.movieId.toLong())
//                } while (it.moveToNext())
//            }
//        }
    }

    class MoviesCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {

        val movie: Movie
            get() = with(Movie()) {
                id = getString(
                        getColumnIndex(MoviesDataBaseContracts.BaseMoviesEntry.COL_ID)
                ).toLong()
                title = getString(
                        getColumnIndex(MoviesDataBaseContracts.BaseMoviesEntry.COL_TITLE)
                )
                releaseDate = getString(
                        getColumnIndex(MoviesDataBaseContracts.BaseMoviesEntry.COL_RELEASE_DATE)
                )
                posterPath = getString(
                        getColumnIndex(MoviesDataBaseContracts.BaseMoviesEntry.COL_POSTER_URL)
                )
                backdropPath = getString(
                        getColumnIndex(MoviesDataBaseContracts.BaseMoviesEntry.COL_BACKDROP_URL)
                )
                overview = getString(
                        getColumnIndex(MoviesDataBaseContracts.BaseMoviesEntry.COL_OVERVIEW)
                )
                voteAverage = getString(
                        getColumnIndex(MoviesDataBaseContracts.BaseMoviesEntry.COL_VOTE_AVERAGE)
                ).toDouble()
                this
            }
    }
}