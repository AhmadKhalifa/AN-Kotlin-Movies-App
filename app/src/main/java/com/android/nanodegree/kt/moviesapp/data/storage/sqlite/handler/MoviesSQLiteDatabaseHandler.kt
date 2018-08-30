package com.android.nanodegree.kt.moviesapp.data.storage.sqlite.handler

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.android.nanodegree.kt.moviesapp.data.model.QueryType
import com.android.nanodegree.kt.moviesapp.data.storage.contract.MoviesDataBaseContracts
import com.android.nanodegree.kt.moviesapp.data.storage.handler.MoviesLocalStorageHandler
import com.android.nanodegree.kt.moviesapp.data.storage.sqlite.SQLiteDatabaseHelper

/**
 * Created by Khalifa on 25/08/2018.
 *
 */
class MoviesSQLiteDatabaseHandler(private val applicationContext: Context) :
        SQLiteDatabaseHelper.SQLiteDatabaseHandler(),
        MoviesLocalStorageHandler {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) = with(sqLiteDatabase) {
        execSQL(MoviesDataBaseContracts.CachedPopularMoviesEntry.creatingQuery)
        execSQL(MoviesDataBaseContracts.CachedTopRatedMoviesEntry.creatingQuery)
        execSQL(MoviesDataBaseContracts.FavoriteMoviesEntry.creatingQuery)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        with(sqLiteDatabase) {
            execSQL(MoviesDataBaseContracts.CachedPopularMoviesEntry.droppingQuery)
            execSQL(MoviesDataBaseContracts.CachedTopRatedMoviesEntry.droppingQuery)
            execSQL(MoviesDataBaseContracts.FavoriteMoviesEntry.droppingQuery)
        }
        onCreate(sqLiteDatabase)
    }

    override fun getMoviesCursor(queryType: QueryType): Cursor = SQLiteDatabaseHelper
            .getInstance(applicationContext).database.query(
            queryType.toString(),
            null,
            null,
            null,
            null,
            null,
            null
    )

    override fun isFavoriteMovie(movieId: String): Boolean  =
            with(SQLiteDatabaseHelper.getInstance(applicationContext).database) {
        val cursor = query(
                MoviesDataBaseContracts.FavoriteMoviesEntry.TABLE_NAME,
                null,
                MoviesDataBaseContracts.BaseMoviesEntry.COL_ID + "=?",
                arrayOf(movieId),
                null,
                null,
                null
        )
        val isFavorite = cursor?.moveToFirst() ?: false
        cursor?.close()
        return isFavorite
    }

    override fun addMovieToFavorites(values: ContentValues) =
            with(SQLiteDatabaseHelper.getInstance(applicationContext).database) {
        if (insertWithOnConflict(
                MoviesDataBaseContracts.FavoriteMoviesEntry.TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        ) == -1L) throw SQLiteException("Error adding movie to favorites")
    }

    override fun removeMovieFromFavorites(movieId: String) =
            with(SQLiteDatabaseHelper.getInstance(applicationContext).database) {
        if (delete(
                MoviesDataBaseContracts.FavoriteMoviesEntry.TABLE_NAME,
                MoviesDataBaseContracts.BaseMoviesEntry.COL_ID + "=?",
                arrayOf(movieId)
        ) == 0) throw SQLiteException("Error removing movie from favorites")
    }

    override fun cacheMovies(queryType: QueryType, moviesContentValues: Array<ContentValues>) =
            with(SQLiteDatabaseHelper.getInstance(applicationContext).database) {
                beginTransaction()
                var rowsInserted = 0
                try {
                    moviesContentValues
                            .filter { insert(queryType.toString(), null, it) != -1L }
                            .forEach { rowsInserted++ }
                    if (rowsInserted == moviesContentValues.size) {
                        setTransactionSuccessful()
                    } else {
                        throw SQLiteException("Error caching all movies.")
                    }
                } finally {
                    endTransaction()
                }
    }

    override fun clearTable(queryType: QueryType): Unit  =
            with(SQLiteDatabaseHelper.getInstance(applicationContext).database) {
                delete(queryType.toString(), null, null)
    }

    override fun loadFavoriteMoviesIds(): Cursor  =
            with(SQLiteDatabaseHelper.getInstance(applicationContext).database) {
                query(
                        MoviesDataBaseContracts.FavoriteMoviesEntry.TABLE_NAME,
                        arrayOf(MoviesDataBaseContracts.BaseMoviesEntry.COL_ID),
                        null,
                        null,
                        null,
                        null,
                        null
                )
    }
}