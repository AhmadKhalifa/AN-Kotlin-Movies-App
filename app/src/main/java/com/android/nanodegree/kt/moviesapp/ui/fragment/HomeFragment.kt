package com.android.nanodegree.kt.moviesapp.ui.fragment

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.android.nanodegree.kt.moviesapp.R
import com.android.nanodegree.kt.moviesapp.data.repository.movies.local.RoomLocalMoviesRepository
import com.android.nanodegree.kt.moviesapp.data.repository.movies.remote.RetrofitRemoteMoviesRepository
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.data.model.QueryType
import com.android.nanodegree.kt.moviesapp.ui.adapter.HomeMoviesAdapter
import com.android.nanodegree.kt.moviesapp.viewmodel.Error
import com.android.nanodegree.kt.moviesapp.viewmodel.Event
import com.android.nanodegree.kt.moviesapp.viewmodel.fragment.home.HomeViewModel
import com.android.nanodegree.kt.moviesapp.ui.base.BaseFragment
import com.android.nanodegree.kt.moviesapp.util.ConnectionChecker
import com.android.nanodegree.kt.moviesapp.util.DimensionsUtil
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.ArrayList

/**
 * Created by Khalifa on 13/08/2018.
 *
 */
class HomeFragment :
        BaseFragment<HomeViewModel>(),
        HomeMoviesAdapter.OnItemInteractionListener {

    companion object {

        val TAG: String = HomeFragment::class.java.simpleName

        private const val LIST_ITEM_MINIMUM_WIDTH_IN_DP = 140

        private const val KEY_QUERY_TYPE = "key_query_type"
        private const val KEY_FAVORITE_MOVIES_IDS_SET = "key_favorite_movies_ids_set"
        private const val KEY_MOST_POPULAR_MOVIES = "key_most_popular_movies"
        private const val KEY_TOP_RATED_MOVIES = "key_top_rated_movies"
        private const val KEY_FAVORITE_MOVIES = "key_favorite_movies"

        @JvmStatic
        fun newInstance(queryType: QueryType?): HomeFragment = queryType?.let {
            HomeFragment().also {
                val arguments = Bundle()
                arguments.putSerializable(KEY_QUERY_TYPE, queryType)
                it.arguments = arguments
            }
        } ?: newInstance(QueryType.MOST_POPULAR)
    }

    private val gridSpanCount: Int
            get() = DimensionsUtil.getGridSpanCount(context, LIST_ITEM_MINIMUM_WIDTH_IN_DP)

    private val moviesAdapter = HomeMoviesAdapter(this)

    private var fragmentInteractionListener: OnFragmentInteractionListener? = null

    private val tryAgainOnClickListener = View.OnClickListener {
        with(viewModel) {
            queryType.value?.let {
                loadMovies(it, true)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(moviesRecyclerView) {
            itemAnimator = DefaultItemAnimator()
            layoutManager = GridLayoutManager(context, gridSpanCount)
            adapter = moviesAdapter
        }
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(KEY_QUERY_TYPE) &&
                savedInstanceState.get(KEY_QUERY_TYPE) != null &&
                savedInstanceState.containsKey(KEY_FAVORITE_MOVIES_IDS_SET) &&
                savedInstanceState.getSerializable(KEY_FAVORITE_MOVIES_IDS_SET) != null &&
                savedInstanceState.containsKey(KEY_MOST_POPULAR_MOVIES) &&
                savedInstanceState.getSerializable(KEY_MOST_POPULAR_MOVIES) != null &&
                savedInstanceState.containsKey(KEY_TOP_RATED_MOVIES) &&
                savedInstanceState.getSerializable(KEY_TOP_RATED_MOVIES) != null &&
                savedInstanceState.containsKey(KEY_FAVORITE_MOVIES) &&
                savedInstanceState.getSerializable(KEY_FAVORITE_MOVIES) != null) {
            with(viewModel) {
                queryType.value =
                        savedInstanceState.get(KEY_QUERY_TYPE) as QueryType
                favoriteMoviesIdsSet.value =
                        savedInstanceState.getLongArray(KEY_FAVORITE_MOVIES_IDS_SET).toHashSet()
                mostPopularMovies.value = Pair(
                        false,
                        savedInstanceState.getParcelableArrayList(KEY_MOST_POPULAR_MOVIES)
                )
                topRatedMovies.value =Pair(
                        false,
                        savedInstanceState.getParcelableArrayList(KEY_TOP_RATED_MOVIES)
                )
                favoriteMovies.value =
                        savedInstanceState.getParcelableArrayList(KEY_FAVORITE_MOVIES)
            }
        } else if (arguments?.containsKey(KEY_QUERY_TYPE)!! &&
                arguments?.get(KEY_QUERY_TYPE) != null) {
            loadMovies(arguments?.get(KEY_QUERY_TYPE) as QueryType)
        }
        viewModel.queryType.value?.let { queryType ->
            viewModel.getMoviesList(queryType)?.let { movies ->
                updateList(movies)
            } ?: loadMovies(queryType)
        } ?: loadMovies(QueryType.MOST_POPULAR)
    }

    // TODO handle this with live data
    override fun onResume() {
        viewModel.loadFavoriteMoviesIds()
        if (viewModel.queryType.value == QueryType.FAVORITES)
            viewModel.loadMovies(QueryType.FAVORITES)
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        with(viewModel) {
            outState.putSerializable(
                    KEY_QUERY_TYPE, queryType.value
            )
            outState.putLongArray(
                    KEY_FAVORITE_MOVIES_IDS_SET, favoriteMoviesIdsSet.value?.toLongArray()
            )
            outState.putParcelableArrayList(
                    KEY_MOST_POPULAR_MOVIES, mostPopularMovies.value?.second as ArrayList<Movie>
            )
            outState.putParcelableArrayList(
                    KEY_TOP_RATED_MOVIES, topRatedMovies.value?.second as ArrayList<Movie>
            )
            outState.putParcelableArrayList(
                    KEY_FAVORITE_MOVIES, favoriteMovies.value as ArrayList<Movie>
            )
        }
        super.onSaveInstanceState(outState)
    }

    override fun onAttach(context: Context?) {
        if (context is OnFragmentInteractionListener) fragmentInteractionListener = context
        else throw IllegalStateException(
                "Context must implement HomeFragment.OnFragmentInteractionListener interface"
        )
        super.onAttach(context)
    }

    override fun onDetach() {
        fragmentInteractionListener = null
        super.onDetach()
    }

    override fun getViewModelInstance() = context?.let {
        HomeViewModel.getInstance(
                this,
                RoomLocalMoviesRepository(it.applicationContext),
                RetrofitRemoteMoviesRepository(
                        ConnectionChecker(it.applicationContext),
                        resources.getString(R.string.tmdb_api_key)
                )
        )
    } ?: throw IllegalStateException("Context is null!")

    override fun handleEvent(event: Event) = when(event) {
        Event.ADDED_TO_FAVORITES,
        Event.REMOVED_FROM_FAVORITES -> showMessage(event.stringResId)
    }

    override fun handleError(error: Error) = when (error) {
        Error.ERROR_LOADING_FAVORITE_MOVIES,
        Error.ERROR_ADDING_MOVIE_TO_FAVORITES,
        Error.ERROR_REMOVING_MOVIE_FROM_FAVORITES ->
            showMessage(error.stringResId)
        Error.NO_INTERNET_CONNECTION,
        Error.NON_STABLE_CONNECTION ->
            showError(
                    R.drawable.ic_cloud_off_white_48px,
                    error.stringResId,
                    true
            )
        Error.GENERAL_ERROR ->
            showError(
                    R.drawable.ic_sentiment_very_dissatisfied_white_48px,
                    error.stringResId,
                    true
            )
        else ->
            showError(
                    R.drawable.ic_sentiment_very_dissatisfied_white_48px,
                    error.stringResId,
                    true
            )
    }

    override fun registerLiveDataObservers() = with(viewModel) {
        fun updateList(queryType: QueryType,
                       movies: List<Movie>?,
                       isRemotelyLoaded: Boolean = false) {
            if (this.queryType.value == queryType) updateList(movies)
            if (!isRemotelyLoaded && queryType != QueryType.FAVORITES && isNetworkAvailable()) {
                showMessage(Snackbar.make(
                        rootView,
                        R.string.refreshing_list,
                        Snackbar.LENGTH_INDEFINITE
                ))
                loadMovies(queryType, true)
            }
        }
        queryType.observe(this@HomeFragment, Observer {
            it?.let { queryType -> {
                updateQueryType(queryType)
            }}
        })
        mostPopularMovies.observe(this@HomeFragment, Observer {
            it?.let { (isRemotelyLoaded, movies) -> {
                updateList(QueryType.MOST_POPULAR, movies, isRemotelyLoaded)
            }}
        })
        topRatedMovies.observe(this@HomeFragment, Observer {
            it?.let { (isRemotelyLoaded, movies) -> {
                updateList(QueryType.TOP_RATED, movies, isRemotelyLoaded)
            }}
        })
        favoriteMovies.observe(this@HomeFragment, Observer {
            it?.let { movies -> {
                updateList(QueryType.FAVORITES, movies)
            }}
        })
        favoriteMoviesIdsSet.observe(this@HomeFragment, Observer {
            it?.let { favoriteMoviesIdsSet -> {
                moviesAdapter.favoriteMoviesIdsSet = favoriteMoviesIdsSet
            }}
        })
    }

    override fun onMovieClick(movie: Movie) = fragmentInteractionListener?.onMovieClick(movie)!!

    override fun onAddMovieToFavorites(movie: Movie) = viewModel.addToFavorites(movie)

    override fun onRemoveMovieFromFavorites(movie: Movie) = viewModel.removeFromFavorites(movie)

    fun loadMovies(queryType: QueryType?) = queryType?.let {
        updateQueryType(it)
        if (queryType != QueryType.FAVORITES && viewModel.getMoviesList(queryType) != null) {
            updateList(viewModel.getMoviesList(queryType))
        } else {
            moviesRecyclerView.visibility = View.GONE
            loadingMoviesProgressBar.visibility = View.VISIBLE
            errorLayout.visibility = View.GONE
            viewModel.loadMovies(it)
        }
    }

    private fun updateQueryType(queryType: QueryType) =
            fragmentInteractionListener?.onQueryTypeChanged(queryType)

    private fun updateList(movies: List<Movie>?) = movies?.let {
        dismissMessagesIfAny()
        if (it.isEmpty()) {
            showError(
                    R.drawable.ic_theaters_white_48px,
                    R.string.nothing_to_show,
                    false
            )
        } else {
            moviesRecyclerView.visibility = View.VISIBLE
            loadingMoviesProgressBar.visibility = View.GONE
            errorLayout.visibility = View.GONE
            moviesAdapter.movies = it
        }
    } ?: showError()

    private fun showError(
            imageResId: Int = R.drawable.ic_sentiment_very_dissatisfied_white_48px,
            messageResId: Int = Error.GENERAL_ERROR.stringResId,
            showTryAgainText: Boolean = true
    ) {
        loadingMoviesProgressBar.visibility = View.GONE
        moviesRecyclerView.visibility = View.GONE
        errorImageView.setImageResource(imageResId)
        errorTextView.setText(messageResId)
        errorLayout.visibility = View.VISIBLE
        errorLayout.setOnClickListener(if (showTryAgainText) tryAgainOnClickListener else null)
        tryAgainTextView.visibility = if (showTryAgainText) View.VISIBLE else View.GONE
    }

    interface OnFragmentInteractionListener {

        fun onMovieClick(movie: Movie?)

        fun onQueryTypeChanged(newQueryType: QueryType)
    }
}
