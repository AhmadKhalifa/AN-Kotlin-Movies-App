package com.android.nanodegree.kt.moviesapp.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.android.nanodegree.kt.moviesapp.R
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.viewmodel.activity.details.MovieDetailsActivityViewModel
import com.android.nanodegree.kt.moviesapp.ui.base.BaseActivity
import com.android.nanodegree.kt.moviesapp.ui.fragment.MovieDetailsFragment
import com.android.nanodegree.kt.moviesapp.viewmodel.Error
import com.android.nanodegree.kt.moviesapp.viewmodel.Event


class MovieDetailsActivity :
        BaseActivity<MovieDetailsActivityViewModel>(),
        MovieDetailsFragment.OnFragmentInteractionListener {

    companion object {

        private const val KEY_MOVIE = "key_movie"

        @JvmStatic
        fun startActivity(context: Context?, movie: Movie?) {
            if (context != null && movie != null) {
                context.startActivity(
                        Intent(context, MovieDetailsActivity::class.java)
                                .putExtra(KEY_MOVIE, movie)
                )
            }
        }
    }

    private var _fragment: MovieDetailsFragment? = null

    private val fragment: MovieDetailsFragment
        get() {
            _fragment = _fragment ?:
                    supportFragmentManager.findFragmentByTag(MovieDetailsFragment.TAG)
                            as MovieDetailsFragment
            _fragment = _fragment ?:
                    MovieDetailsFragment.newInstance(viewModel.movie)
            return _fragment as MovieDetailsFragment
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        viewModel.movie = viewModel.movie ?: if (savedInstanceState?.containsKey(KEY_MOVIE)!! &&
                savedInstanceState.getParcelable<Movie>(KEY_MOVIE) != null) {
            savedInstanceState.getParcelable(KEY_MOVIE)
        } else if (intent?.hasExtra(KEY_MOVIE)!! &&
                intent.getParcelableExtra<Movie>(KEY_MOVIE) != null) {
            intent.getParcelableExtra<Movie>(KEY_MOVIE)
        } else {
            null
        }
        viewModel.movie?.let { movie ->
            title = movie.title
            if (supportFragmentManager.findFragmentByTag(MovieDetailsFragment.TAG) == null) {
                supportFragmentManager
                        .beginTransaction()
                        .add(R.id.fragmentPlaceHolderLayout, fragment, MovieDetailsFragment.TAG)
                        .commit()
            }
        } ?: throw IllegalStateException("Invalid movie value!")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable(KEY_MOVIE, viewModel.movie)
        super.onSaveInstanceState(outState)
    }

    override fun onTrailerClick(trailer: Movie.Trailer?) = trailer?.let {
        if (!TextUtils.isEmpty(it.videoUrl)) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.videoUrl))
            if (intent.resolveActivity(packageManager) != null)
                startActivity(intent)
        }
    }!!

    override fun getViewModelInstance(): MovieDetailsActivityViewModel =
            MovieDetailsActivityViewModel.getInstance(this)

    override fun handleEvent(event: Event) {
    }

    override fun handleError(error: Error) {

    }

    override fun registerLiveDataObservers() {

    }
}
