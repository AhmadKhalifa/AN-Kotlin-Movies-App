package com.android.nanodegree.kt.moviesapp.data.storage.content

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.sqlite.SQLiteException
import android.net.Uri
import com.android.nanodegree.kt.moviesapp.data.model.QueryType
import com.android.nanodegree.kt.moviesapp.data.storage.contract.MoviesDataBaseContracts
import com.android.nanodegree.kt.moviesapp.data.storage.sqlite.SQLiteDatabaseHelper

/**
 * Created by Khalifa on 25/08/2018.
 *
 */
class MoviesContentProvider : ContentProvider() {

    companion object {

        private const val PROVIDER_NAME =
                "com.android.nanodegree.kt.moviesapp.data.storage.content.MoviesContentProvider"

        private const val MOST_POPULAR_ENDPOINT = "mostPopularMovies"
        private const val TOP_RATED_ENDPOINT = "topRatedMovies"
        private const val FAVORITES_ENDPOINT = "favoriteMovies"

        private const val MOST_POPULAR_URI = "content://$PROVIDER_NAME/$MOST_POPULAR_ENDPOINT"
        private const val TOP_RATED_URI = "content://$PROVIDER_NAME/$TOP_RATED_ENDPOINT"
        private const val FAVORITES_URI = "content://$PROVIDER_NAME/$FAVORITES_ENDPOINT"

        private const val MOST_POPULAR_MOVIES = 1
        private const val TOP_RATED_MOVIES = 2
        private const val FAVORITE_MOVIES = 3

        private val sUriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH).also {
            it.addURI(PROVIDER_NAME, MOST_POPULAR_ENDPOINT, MOST_POPULAR_MOVIES)
            it.addURI(PROVIDER_NAME, TOP_RATED_ENDPOINT, TOP_RATED_MOVIES)
            it.addURI(PROVIDER_NAME, FAVORITES_ENDPOINT, FAVORITE_MOVIES)
        }

        val MOST_POPULAR_CONTENT_URI: Uri = Uri.parse(MOST_POPULAR_URI)
        val TOP_RATED_CONTENT_URI: Uri = Uri.parse(TOP_RATED_URI)
        val FAVORITES_CONTENT_URI: Uri = Uri.parse(FAVORITES_URI)

        fun getTableUri(queryType: QueryType): Uri {
            return when (queryType) {
                QueryType.MOST_POPULAR -> MoviesContentProvider.MOST_POPULAR_CONTENT_URI
                QueryType.TOP_RATED -> MoviesContentProvider.TOP_RATED_CONTENT_URI
                QueryType.FAVORITES -> MoviesContentProvider.FAVORITES_CONTENT_URI
            }
        }
    }

    override fun onCreate() = true

    override fun insert(uri: Uri, values: ContentValues): Uri {
        val (tableName, tableContentUri) = getTableDetails(uri)
        val insertedItemId = SQLiteDatabaseHelper.getInstance(context.applicationContext)
                .database.insert(tableName, null, values)
        if (insertedItemId > 0) {
            val insertedItemUri = ContentUris.withAppendedId(tableContentUri, insertedItemId)
            context.contentResolver.notifyChange(insertedItemUri, null)
            return insertedItemUri
        } else throw SQLiteException("Failed to insert a row into $uri")
    }

    override fun bulkInsert(uri: Uri, values: Array<out ContentValues>) =
            with(SQLiteDatabaseHelper.getInstance(context.applicationContext).database) {
                beginTransaction()
                var rowsInserted = 0
                try {
                    values.forEach { movieValue ->
                        if (insert(getTableName(uri), null, movieValue) != -1L)
                            rowsInserted++
                    }
                    if (rowsInserted == values.size) setTransactionSuccessful()
                    else throw SQLiteException("Error caching all movies")
                } finally {
                    endTransaction()
                }
                rowsInserted
            }

    override fun query(uri: Uri?,
                       projection: Array<out String>?,
                       selection: String?,
                       selectionArgs: Array<out String>?,
                       sortOrder: String?) = uri?.let {
        with(SQLiteDatabaseHelper.getInstance(context.applicationContext).database.query(
                getTableName(uri),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        )) {
            setNotificationUri(context.contentResolver, uri)
            close()
            this@with
        }
    } ?: throw IllegalArgumentException("Invalid uri!")

    override fun update(uri: Uri?,
                        values: ContentValues?,
                        selection: String?,
                        selectionArgs: Array<out String>?) = uri?.let {
        SQLiteDatabaseHelper.getInstance(context.applicationContext).database.update(
                getTableName(uri),
                values,
                selection,
                selectionArgs
        )
    } ?: throw IllegalArgumentException("Invalid uri!")

    override fun delete(uri: Uri?,
                        selection: String?,
                        selectionArgs: Array<out String>?) = uri?.let {
        SQLiteDatabaseHelper.getInstance(context.applicationContext).database.delete(
                getTableName(uri),
                selection,
                selectionArgs
        )
    } ?: throw IllegalArgumentException("Invalid uri!")

    override fun getType(uri: Uri?) = when (sUriMatcher.match(uri)) {
        MOST_POPULAR_MOVIES -> "MOST_POPULAR_MOVIES"
        TOP_RATED_MOVIES -> "TOP_RATED_MOVIES"
        FAVORITE_MOVIES -> "FAVORITE_MOVIES"
        else -> throw IllegalStateException("Unsupported URI: $uri")
    }

    private fun getTableName(uri: Uri) = getTableDetails(uri).first

    private fun getTableDetails(uri: Uri) = when (sUriMatcher.match(uri)) {
        MOST_POPULAR_MOVIES -> Pair(
                MoviesDataBaseContracts.CachedPopularMoviesEntry.TABLE_NAME,
                MOST_POPULAR_CONTENT_URI
        )
        TOP_RATED_MOVIES -> Pair(
                MoviesDataBaseContracts.CachedTopRatedMoviesEntry.TABLE_NAME,
                TOP_RATED_CONTENT_URI
        )
        FAVORITE_MOVIES -> Pair(
                MoviesDataBaseContracts.FavoriteMoviesEntry.TABLE_NAME,
                FAVORITES_CONTENT_URI
        )
        else -> throw IllegalArgumentException("Unsupported URI: " + uri)
    }
}
