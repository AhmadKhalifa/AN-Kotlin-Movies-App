package com.android.nanodegree.kt.moviesapp.data.storage.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.android.nanodegree.kt.moviesapp.data.storage.sqlite.handler.MoviesSQLiteDatabaseHandler

/**
 * Created by Khalifa on 25/08/2018.
 *
 */
class SQLiteDatabaseHelper private constructor(applicationContext: Context) :
        SQLiteOpenHelper(applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private const val MOVIES_DATABASE_HANDLER_INDEX = 0

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "MoviesAppDB.db"

        private var sInstance: SQLiteDatabaseHelper? = null

        fun getInstance(applicationContext: Context): SQLiteDatabaseHelper {
            sInstance = sInstance ?: SQLiteDatabaseHelper(applicationContext)
            return if (sInstance != null) sInstance as SQLiteDatabaseHelper
            else throw IllegalStateException("SQLiteDatabaseHelper is null!")
        }
    }

    private var _database: SQLiteDatabase? = null
    val database: SQLiteDatabase
        get() {
            _database = _database ?: writableDatabase
            return if (_database != null) _database as SQLiteDatabase
            else throw IllegalStateException("Database is null!")
        }

    private val databaseHandlers: Array<SQLiteDatabaseHandler> = arrayOf(
            MoviesSQLiteDatabaseHandler(applicationContext)
    )

    fun getMoviesDatabaseHandler() =
            databaseHandlers[MOVIES_DATABASE_HANDLER_INDEX] as MoviesSQLiteDatabaseHandler

    override fun onCreate(sqLiteDatabase: SQLiteDatabase?): Unit = sqLiteDatabase?.let { db ->
        databaseHandlers.forEach { it.onCreate(db) }
    }!!

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase?,
                           oldVersion: Int, newVersion: Int): Unit = sqLiteDatabase?.let { db ->
        databaseHandlers.forEach { it.onUpgrade(db, oldVersion, newVersion) }
    }!!

    abstract class SQLiteDatabaseHandler {

        abstract fun onCreate(sqLiteDatabase: SQLiteDatabase)

        abstract fun onUpgrade(sqLiteDatabase: SQLiteDatabase,
                                         oldVersion: Int,
                                         newVersion: Int)
    }
}