package com.android.nanodegree.kt.moviesapp.data.repository.movies.remote

import android.arch.lifecycle.MutableLiveData
import com.android.nanodegree.kt.moviesapp.data.payload.MoviesResponse
import com.android.nanodegree.kt.moviesapp.data.payload.ReviewsResponse
import com.android.nanodegree.kt.moviesapp.data.payload.TrailersResponse
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.data.model.QueryType
import com.android.nanodegree.kt.moviesapp.util.BaseConnectionChecker
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
class RetrofitRemoteMoviesRepository(
        connectionChecker: BaseConnectionChecker,
        apiKey: String?
) : BaseRemoteMoviesRepository(connectionChecker, apiKey) {

    interface MovieService {

        @GET("/3/movie/{query_type}")
        fun loadMovies(@Path("query_type") queryType: String,
                       @Query("api_key") apiKey: String): Call<MoviesResponse>

        @GET("/3/movie/{movie_id}/videos")
        fun loadMovieTrailers(@Path("movie_id") movieId: String,
                              @Query("api_key") apiKey: String): Call<TrailersResponse>

        @GET("/3/movie/{movie_id}/reviews")
        fun loadMovieReviews(@Path("movie_id") movieId: String,
                             @Query("api_key") apiKey: String): Call<ReviewsResponse>
    }

    @Throws(Exception::class)
    override fun loadMovies(queryType: QueryType) = try {
        val response: MoviesResponse? = execute(
                create(MovieService::class.java).loadMovies(queryType.toString(), apiKey!!)
        )
        response ?: throw Exception("Error getting movies")
        MutableLiveData<List<Movie>>().also { it.value = response.movies }
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
        throw Exception(throwable)
    }

    @Throws(Exception::class)
    override fun loadMovieTrailers(movieId: String): List<Movie.Trailer>? = try {
        val response: TrailersResponse? = execute(
                create(MovieService::class.java).loadMovieTrailers(movieId, apiKey!!)
        )
        response ?: throw Exception("Error getting movie trailers")
        response.trailers
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
        throw Exception(throwable)
    }

    @Throws(Exception::class)
    override fun loadMoviesReviews(movieId: String): List<Movie.Review>?  = try {
        val response: ReviewsResponse? = execute(
                create(MovieService::class.java).loadMovieReviews(movieId, apiKey!!)
        )
        response ?: throw Exception("Error getting movie reviews")
        response.reviews
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
        throw Exception(throwable)
    }

}
