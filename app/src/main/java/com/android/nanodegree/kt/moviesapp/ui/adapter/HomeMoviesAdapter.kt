package com.android.nanodegree.kt.moviesapp.ui.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.nanodegree.kt.moviesapp.R
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.util.DateFormatter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.grid_item_movie.view.*

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
class HomeMoviesAdapter(private val itemInteractionListener: OnItemInteractionListener?) :
        RecyclerView.Adapter<HomeMoviesAdapter.HomeMoviesViewHolder>() {

    var movies: List<Movie>? = null
        set(value) = notifyDataSetChanged()

    var favoriteMoviesIdsSet: HashSet<Long>? = null
        set(value) = notifyDataSetChanged()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HomeMoviesViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.grid_item_movie, parent, false)
    )

    override fun getItemCount() = movies?.size ?: 0

    override fun onBindViewHolder(holder: HomeMoviesViewHolder, position: Int) {
        holder.setContent(this)
    }

    class HomeMoviesViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun setContent(adapter: HomeMoviesAdapter) = with(view) {
            val currentMovie = adapter.movies?.get(adapterPosition)
            currentMovie?.let { movie ->
                with(favoriteOrUnfavoriteImageButton) {
                    if (adapter.favoriteMoviesIdsSet != null) {
                        visibility = View.VISIBLE
                        setImageDrawable(ContextCompat.getDrawable(
                                context,
                                if (adapter.favoriteMoviesIdsSet?.contains(movie.id)!!)
                                    R.drawable.ic_favorite_white
                                else
                                    R.drawable.ic_favorite_border_white)
                        )
                    } else {
                        visibility = View.GONE
                    }
                }
                Picasso.get().load(movie.posterPathUrl).into(moviePosterImageView)
                movieNameTextView.text = movie.title
                movieReleaseDateTextView.text = DateFormatter.format(movie.releaseDate)
                movieRateTextView.text = movie.voteAverage.toString()
                adapter.itemInteractionListener?.let { listener ->
                    movieItemCardView.setOnClickListener { listener.onMovieClick(movie) }
                    favoriteOrUnfavoriteImageButton.setOnClickListener {
                        if (adapter.favoriteMoviesIdsSet?.contains(movie.id)!!)
                            listener.onRemoveMovieFromFavorites(movie)
                        else
                            listener.onAddMovieToFavorites(movie)
                    }
                }
            }
        }
    }

    interface OnItemInteractionListener {

        fun onMovieClick(movie: Movie)

        fun onAddMovieToFavorites(movie: Movie)

        fun onRemoveMovieFromFavorites(movie: Movie)
    }
}
