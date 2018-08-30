package com.android.nanodegree.kt.moviesapp.data.storage.contract

import android.content.ContentValues
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.data.model.QueryType

/**
 * Created by Khalifa on 25/08/2018.
 *
 */
object MoviesDataBaseContracts {

    interface BaseMoviesEntry {
        companion object {
            const val COL_ID = "_id"
            const val COL_TITLE = "title"
            const val COL_RELEASE_DATE = "release_date"
            const val COL_POSTER_URL = "poster_url"
            const val COL_BACKDROP_URL = "backdrop_url"
            const val COL_OVERVIEW = "overview"
            const val COL_VOTE_AVERAGE = "vote_average"
        }
    }

    fun getCreatingQuery(tableName: String) =
            "CREATE TABLE IF NOT EXISTS $tableName (" +
                    "${BaseMoviesEntry.COL_ID} TEXT PRIMARY KEY, " +
                    "${BaseMoviesEntry.COL_TITLE} TEXT, " +
                    "${BaseMoviesEntry.COL_RELEASE_DATE} TEXT, " +
                    "${BaseMoviesEntry.COL_POSTER_URL} TEXT, " +
                    "${BaseMoviesEntry.COL_BACKDROP_URL} TEXT, " +
                    "${BaseMoviesEntry.COL_OVERVIEW} TEXT, " +
                    "${BaseMoviesEntry.COL_VOTE_AVERAGE} TEXT);"
    
    fun getDroppingQuery(tableName: String) = "DROP TABLE IF EXISTS $tableName;"

    class CachedPopularMoviesEntry : BaseMoviesEntry {
        companion object {

            val TABLE_NAME = QueryType.MOST_POPULAR.toString()

            val creatingQuery: String get() = MoviesDataBaseContracts.getCreatingQuery(TABLE_NAME)

            val droppingQuery: String get() = MoviesDataBaseContracts.getDroppingQuery(TABLE_NAME)
        }
    }

    class CachedTopRatedMoviesEntry : BaseMoviesEntry {
        companion object {

            val TABLE_NAME = QueryType.TOP_RATED.toString()

            val creatingQuery: String get() = MoviesDataBaseContracts.getCreatingQuery(TABLE_NAME)

            val droppingQuery: String get() = MoviesDataBaseContracts.getDroppingQuery(TABLE_NAME)
        }
    }

    class FavoriteMoviesEntry : BaseMoviesEntry {
        companion object {

            val TABLE_NAME = QueryType.FAVORITES.toString()

            val creatingQuery: String get() = MoviesDataBaseContracts.getCreatingQuery(TABLE_NAME)

            val droppingQuery: String get() = MoviesDataBaseContracts.getDroppingQuery(TABLE_NAME)
        }
    }

    fun getMovieContentValues(movie: Movie) = with(ContentValues()) {
        put(BaseMoviesEntry.COL_ID, movie.id.toString())
        put(BaseMoviesEntry.COL_TITLE, movie.title)
        put(BaseMoviesEntry.COL_RELEASE_DATE, movie.releaseDate)
        put(BaseMoviesEntry.COL_POSTER_URL, movie.posterPath)
        put(BaseMoviesEntry.COL_BACKDROP_URL, movie.backdropPath)
        put(BaseMoviesEntry.COL_OVERVIEW, movie.overview)
        put(BaseMoviesEntry.COL_VOTE_AVERAGE, movie.voteAverage.toString())
        this
    }

    fun getMoviesContentValuesArray(movies: List<Movie>?) = movies?.let {
        List(it.size, { index -> getMovieContentValues(it[index]) }).toTypedArray()
    } ?: arrayOf()
}
