package com.android.nanodegree.kt.moviesapp.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.nanodegree.kt.moviesapp.R
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import kotlinx.android.synthetic.main.list_item_review.view.*

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
class MovieReviewsAdapter : RecyclerView.Adapter<MovieReviewsAdapter.MoviesReviewsViewHolder>() {

    var reviews: List<Movie.Review>? = null
        set(value) = notifyDataSetChanged()

    override fun onBindViewHolder(holder: MoviesReviewsViewHolder, position: Int) {
            holder.setContent(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MoviesReviewsViewHolder(
        LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_review, parent, false)
    )

    override fun getItemCount() = reviews?.size ?: 0

    class MoviesReviewsViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun setContent(adapter: MovieReviewsAdapter) = with(view) {
            val currentReview = adapter.reviews?.get(adapterPosition)
            currentReview?.let { review ->
                reviewAuthorTextView.text = review.author
                reviewContentTextView.text = review.content
            }
        }
    }
}
