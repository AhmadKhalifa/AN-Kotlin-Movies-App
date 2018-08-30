package com.android.nanodegree.kt.moviesapp.data.repository

import com.android.nanodegree.kt.moviesapp.exception.NoInternetConnectionException
import com.android.nanodegree.kt.moviesapp.util.BaseConnectionChecker
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
interface Repository

open class BaseLocalRepository : Repository

open class BaseRemoteRepository(
        private val connectionChecker: BaseConnectionChecker?,
        private val baseUrl: String?
) : Repository {

    private val serviceMap: MutableMap<Class<*>, Any> = HashMap()

    @Suppress("UNCHECKED_CAST")
    protected fun <T> create(clazz: Class<T>): T {
        val service: T
        if (serviceMap.containsKey(clazz)) {
            service = serviceMap[clazz] as T
        } else {
            service = retrofit().create(clazz)
            serviceMap.put(clazz, service!!)
        }
        return service
    }

    @Throws(Throwable::class)
    protected fun <T> execute(call: Call<T>): T {
        if (!isNetworkAvailable()) {
            throw NoInternetConnectionException()
        }
        val response: Response<T> = call.execute()
        if (!response.isSuccessful) {
            throw Throwable(response.message())
        }
        return response.body()!!
    }


    private fun retrofit(): Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl!!)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                    OkHttpClient.Builder()
                            .readTimeout(60, TimeUnit.SECONDS)
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .build()
    ).build()

    fun isNetworkAvailable() = connectionChecker?.isNetworkAvailable() ?: false
}