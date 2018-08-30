package com.android.nanodegree.kt.moviesapp.data.storage.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.content.Context
import com.android.nanodegree.kt.moviesapp.data.model.Movie

/**
 * Created by Khalifa on 17/08/2018.
 *
 */
@Database(entities = arrayOf(Movie::class), version = 1, exportSchema = false)
abstract class MoviesDatabase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "MoviesRoomDatabase.db"

        private var sInstance: MoviesDatabase? = null

        fun getInstance(applicationContext: Context): MoviesDatabase {
            if (sInstance == null)
                sInstance = Room.databaseBuilder(
                        applicationContext,
                        MoviesDatabase::class.java,
                        DATABASE_NAME
                ).build()
            if (sInstance == null)
                throw IllegalStateException("Couldn't instantiate MoviesDatabase")
            return sInstance!!
        }
    }

    abstract fun moviesDao(): MoviesDao
}

@Dao
interface MoviesDao {

    @Query("SELECT * FROM movies where is_most_popular = 1")
    fun getMostPopularMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies where is_top_rated = 1")
    fun getTopRatedMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies where is_favorite = 1")
    fun getFavoriteMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movies where id IN (:moviesIds)")
    fun getMoviesByIds(moviesIds: LongArray): LiveData<List<Movie>>

    @Insert(onConflict = REPLACE)
    fun addOrUpdateMovies(vararg movies: Movie)

    @Insert(onConflict = REPLACE)
    fun addOrUpdateMovie(movie: Movie)

    @Query("SELECT * FROM movies WHERE id = (:movieId)")
    fun getMovie(movieId: String): LiveData<Movie?>
}
