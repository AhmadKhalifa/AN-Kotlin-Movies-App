package com.android.nanodegree.kt.moviesapp.data.storage.content.handler

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.android.nanodegree.kt.moviesapp.data.model.QueryType
import com.android.nanodegree.kt.moviesapp.data.storage.content.MoviesContentProvider
import com.android.nanodegree.kt.moviesapp.data.storage.contract.MoviesDataBaseContracts
import com.android.nanodegree.kt.moviesapp.data.storage.handler.MoviesLocalStorageHandler

/**
 * Created by Khalifa on 25/08/2018.
 *
 */
class MoviesContentProivderHandler(private val applicationContext: Context) :
        MoviesLocalStorageHandler {

    override fun getMoviesCursor(queryType: QueryType): Cursor =
            applicationContext.contentResolver.query(
                    MoviesContentProvider.getTableUri(queryType),
                    null,
                    null,
                    null,
                    null,
                    null
            )

    override fun isFavoriteMovie(movieId: String): Boolean {
        val cursor = applicationContext.contentResolver.query(
                MoviesContentProvider.FAVORITES_CONTENT_URI, null,
                MoviesDataBaseContracts.BaseMoviesEntry.COL_ID + "=?",
                arrayOf(movieId), null, null

        )
        val isFavoriteMovie = cursor?.moveToFirst() ?: false
        cursor?.close()
        return isFavoriteMovie
    }

    override fun addMovieToFavorites(values: ContentValues) {
        applicationContext.contentResolver.insert(
                MoviesContentProvider.FAVORITES_CONTENT_URI,
                values
        )
    }

    override fun removeMovieFromFavorites(movieId: String) {
        applicationContext.contentResolver.delete(
                MoviesContentProvider.FAVORITES_CONTENT_URI,
                MoviesDataBaseContracts.BaseMoviesEntry.COL_ID + "=?",
                arrayOf(movieId)
        )
    }

    override fun cacheMovies(queryType: QueryType, moviesContentValues: Array<ContentValues>) {
        applicationContext.contentResolver.bulkInsert(
                MoviesContentProvider.getTableUri(queryType),
                moviesContentValues
        )
    }

    override fun clearTable(queryType: QueryType) {
        applicationContext.contentResolver.delete(
                MoviesContentProvider.getTableUri(queryType),
                null,
                null
        )
    }

    override fun loadFavoriteMoviesIds(): Cursor = applicationContext.contentResolver.query(
            MoviesContentProvider.FAVORITES_CONTENT_URI,
            arrayOf(MoviesDataBaseContracts.BaseMoviesEntry.COL_ID),
            null,
            null,
            null,
            null
    )
}