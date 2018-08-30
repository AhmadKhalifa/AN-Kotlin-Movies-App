package com.android.nanodegree.kt.moviesapp.data.storage.handler

import android.content.ContentValues
import android.database.Cursor
import com.android.nanodegree.kt.moviesapp.data.model.QueryType

/**
 * Created by Khalifa on 25/08/2018.
 *
 */
interface MoviesLocalStorageHandler {

    fun getMoviesCursor(queryType: QueryType): Cursor

    fun isFavoriteMovie(movieId: String): Boolean

    fun addMovieToFavorites(values: ContentValues)

    fun removeMovieFromFavorites(movieId: String)

    fun cacheMovies(queryType: QueryType, moviesContentValues: Array<ContentValues>)

    fun clearTable(queryType: QueryType)

    fun loadFavoriteMoviesIds(): Cursor
}