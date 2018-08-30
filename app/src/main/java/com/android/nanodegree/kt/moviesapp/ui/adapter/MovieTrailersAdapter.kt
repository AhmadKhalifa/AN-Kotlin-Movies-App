package com.android.nanodegree.kt.moviesapp.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.nanodegree.kt.moviesapp.R
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_trailer.view.*

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
class MovieTrailersAdapter(private val itemInteractionListener: OnItemInteractionListener?) :
        RecyclerView.Adapter<MovieTrailersAdapter.MovieTrailersViewHolder>() {

    var trailers: List<Movie.Trailer>? = null
        set(value) = notifyDataSetChanged()

    override fun getItemCount() = trailers?.size ?: 0

    override fun onBindViewHolder(holder: MovieTrailersViewHolder, position: Int) {
        holder.setContent(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MovieTrailersViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_trailer, parent, false)
    )

    class MovieTrailersViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun setContent(adapter: MovieTrailersAdapter) = with(view) {
            val currentTrailer = adapter.trailers?.get(adapterPosition)
            currentTrailer?.let { trailer ->
                Picasso.get().load(trailer.thumbnailImageUrl).into(trailerThumbnailImageView)
                trailerNameTextView.text = trailer.name
                adapter.itemInteractionListener?.let { listener ->
                    trailerCardView.setOnClickListener { listener.onTrailerClick(trailer) }
                }
            }
        }
    }

    interface OnItemInteractionListener {

        fun onTrailerClick(trailer: Movie.Trailer)
    }
}