package com.android.nanodegree.kt.moviesapp.ui.fragment

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.nanodegree.kt.moviesapp.R
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.data.repository.movies.local.RoomLocalMoviesRepository
import com.android.nanodegree.kt.moviesapp.data.repository.movies.remote.RetrofitRemoteMoviesRepository
import com.android.nanodegree.kt.moviesapp.ui.adapter.MovieReviewsAdapter
import com.android.nanodegree.kt.moviesapp.ui.adapter.MovieTrailersAdapter
import com.android.nanodegree.kt.moviesapp.viewmodel.Error
import com.android.nanodegree.kt.moviesapp.viewmodel.Event
import com.android.nanodegree.kt.moviesapp.viewmodel.fragment.details.MovieDetailsViewModel
import com.android.nanodegree.kt.moviesapp.ui.base.BaseFragment
import com.android.nanodegree.kt.moviesapp.util.ConnectionChecker
import com.android.nanodegree.kt.moviesapp.util.DateFormatter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_movie_details.*
import java.util.ArrayList

/**
 * Created by Khalifa on 13/08/2018.
 *
 */
class MovieDetailsFragment :
        BaseFragment<MovieDetailsViewModel>(),
        MovieTrailersAdapter.OnItemInteractionListener {

    companion object {

        val TAG: String = MovieDetailsFragment::class.java.simpleName

        private const val KEY_MOVIE = "key_movie"
        private const val KEY_IS_FAVORITE = "key_is_favorite"
        private const val KEY_TRAILERS = "key_trailers"
        private const val KEY_REVIEWS = "key_reviews"

        @JvmStatic
        fun newInstance(movie: Movie?): MovieDetailsFragment = movie?.let {
            MovieDetailsFragment().also {
                val arguments = Bundle()
                arguments.putParcelable(KEY_MOVIE, movie)
                it.arguments = arguments
            }
        } ?: throw IllegalArgumentException("Movie cannot be null!")
    }

    private val trailersAdapter = MovieTrailersAdapter(this)
    private val reviewsAdapter = MovieReviewsAdapter()

    private val fragmentInteractionListener: OnFragmentInteractionListener? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_movie_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(trailersRecyclerView) {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
            )
            isNestedScrollingEnabled = false
            adapter = trailersAdapter
        }
        with(reviewsRecyclerView) {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = reviewsAdapter
        }
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(KEY_MOVIE) &&
                savedInstanceState.getParcelable<Movie>(KEY_MOVIE) != null &&
                savedInstanceState.containsKey(KEY_IS_FAVORITE)) {
            with(viewModel) {
                movie.value = savedInstanceState.getParcelable(KEY_MOVIE)
                isFavorite.value = savedInstanceState.getBoolean(KEY_IS_FAVORITE)
                if (savedInstanceState.containsKey(KEY_TRAILERS) &&
                        savedInstanceState.getParcelableArrayList<Movie.Trailer>(KEY_TRAILERS) != null) {
                    trailers.value = savedInstanceState.getParcelableArrayList(KEY_TRAILERS)
                } else loadTrailers()
                if (savedInstanceState.containsKey(KEY_REVIEWS) &&
                        savedInstanceState.getParcelableArrayList<Movie.Review>(KEY_REVIEWS) != null) {
                    reviews.value = savedInstanceState.getParcelableArrayList(KEY_REVIEWS)
                } else loadReviews()
            }
        } else if (arguments?.containsKey(KEY_MOVIE)!! &&
                arguments?.getParcelable<Movie>(KEY_MOVIE) != null) {
            viewModel.movie.value = arguments?.getParcelable(KEY_MOVIE)
        } else throw IllegalStateException("Invalid movie!")
        favoriteButton.setOnClickListener {
            if (favoriteButton.text != null &&
                    getString(R.string.add_to_favorites) == favoriteButton.text.toString()) {
                viewModel.addToFavorites(viewModel.movie.value)
            } else {
                viewModel.removeFromFavorites(viewModel.movie.value)
            }
        }
    }

    private fun loadTrailers() {
        trailersRecyclerView.visibility = View.GONE
        noTrailersTextView.visibility = View.GONE
        loadingTrailersProgressBar.visibility = View.VISIBLE
        viewModel.loadMoviesTrailers(viewModel.movie.value?.id?.toString())
    }

    private fun loadReviews() {
        reviewsRecyclerView.visibility = View.GONE
        noReviewsTextView.visibility = View.GONE
        loadingReviewsProgressBar.visibility = View.VISIBLE
        viewModel.loadMovieReviews(viewModel.movie.value?.id?.toString())
    }

    private fun showMovieDetails(movie: Movie?) = movie?.let {
        Picasso.get().load(it.backdropPathUrl).into(posterImageView)
        titleTextView.text = it.title
        releaseDateTextView.text = DateFormatter.format(it.releaseDate)
        rateTextView.text = it.voteAverage.toString()
        overviewTextView.text = it.overview
        viewModel.isFavoriteMovie(it.id.toString())
    }

    private fun showTrailers(trailers: List<Movie.Trailer>?) {
        loadingTrailersProgressBar.visibility = View.GONE
        if (trailers != null) {
            if (trailers.isEmpty()) {
                noTrailersTextView.visibility = View.VISIBLE
                noTrailersTextView.setText(R.string.details_no_trailers_available)
                trailersRecyclerView.visibility = View.VISIBLE
            } else {
                trailersRecyclerView.visibility = View.VISIBLE
                noTrailersTextView.visibility = View.GONE
                trailersAdapter.trailers = trailers
            }
        } else {
            noTrailersTextView.visibility = View.VISIBLE
            noTrailersTextView.setText(R.string.error_loading_movie_trailers)
            trailersRecyclerView.visibility = View.GONE
        }
    }

    private fun showReviews(reviews: List<Movie.Review>?) {
        loadingReviewsProgressBar.visibility = View.GONE
        if (reviews != null) {
            if (reviews.isEmpty()) {
                noReviewsTextView.visibility = View.VISIBLE
                noReviewsTextView.setText(R.string.details_no_reviews_available)
                reviewsRecyclerView.visibility = View.VISIBLE
            } else {
                reviewsRecyclerView.visibility = View.VISIBLE
                noReviewsTextView.visibility = View.GONE
                reviewsAdapter.reviews = reviews
            }
        } else {
            noReviewsTextView.visibility = View.VISIBLE
            noReviewsTextView.setText(R.string.error_loading_movie_trailers)
            reviewsRecyclerView.visibility = View.GONE
        }
    }

    private fun updateFavoriteState(isFavorite: Boolean) {
        if (isFavorite) {
            favoriteButton.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.button_yellow, null)
            favoriteButton.setText(R.string.remove_from_favorites)
        } else {
            favoriteButton.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.button_green, null)
            favoriteButton.setText(R.string.add_to_favorites)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        with(viewModel) {
            outState.putParcelable(
                    KEY_MOVIE, movie.value
            )
            outState.putBoolean(
                    KEY_IS_FAVORITE, isFavorite.value ?: false
            )
            outState.putParcelableArrayList(
                    KEY_TRAILERS, trailers.value as ArrayList<Movie.Trailer>
            )
            outState.putParcelableArrayList(
                    KEY_REVIEWS, reviews.value as ArrayList<Movie.Review>
            )
        }
        super.onSaveInstanceState(outState)
    }

    override fun getViewModelInstance() = context?.let {
        MovieDetailsViewModel.getInstance(
                this,
                RoomLocalMoviesRepository(it.applicationContext),
                RetrofitRemoteMoviesRepository(
                        ConnectionChecker(it.applicationContext),
                        resources.getString(R.string.tmdb_api_key)
                )
        )
    } ?: throw IllegalStateException("Context is null!")

    override fun handleEvent(event: Event) = when (event) {
        Event.ADDED_TO_FAVORITES,
        Event.REMOVED_FROM_FAVORITES -> showMessage(event.stringResId)
    }

    override fun handleError(error: Error) = when (error) {
        Error.ERROR_LOADING_MOVIE_TRAILERS -> viewModel.trailers.value = null
        Error.ERROR_LOADING_MOVIE_REVIEWS -> viewModel.reviews.value = null
        else -> showMessage(error.stringResId)
    }

    override fun registerLiveDataObservers() = with(viewModel) {
        movie.observe(this@MovieDetailsFragment, Observer {
            movie -> showMovieDetails(movie)
        })
        isFavorite.observe(this@MovieDetailsFragment, Observer {
            isFavorite -> updateFavoriteState(isFavorite ?: false)
        })
        trailers.observe(this@MovieDetailsFragment, Observer {
            trailers -> showTrailers(trailers)
        })
        reviews.observe(this@MovieDetailsFragment, Observer {
            reviews -> showReviews(reviews)
        })
    }

    override fun onTrailerClick(trailer: Movie.Trailer) =
            fragmentInteractionListener?.onTrailerClick(trailer)!!

    interface OnFragmentInteractionListener {

        fun onTrailerClick(trailer: Movie.Trailer?)
    }
}