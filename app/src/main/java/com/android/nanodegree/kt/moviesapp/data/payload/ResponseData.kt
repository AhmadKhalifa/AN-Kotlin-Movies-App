package com.android.nanodegree.kt.moviesapp.data.payload

import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.google.gson.annotations.SerializedName

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
class MoviesResponse {

    @SerializedName("results")
    val movies: List<Movie>? = null
}

class ReviewsResponse {

    @SerializedName("results")
    val reviews: List<Movie.Review>? = null
}

class TrailersResponse {

    @SerializedName("result")
    val trailers: List<Movie.Trailer>? = null
}