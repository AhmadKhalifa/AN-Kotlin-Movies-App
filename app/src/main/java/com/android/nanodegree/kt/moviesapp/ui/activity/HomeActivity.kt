package com.android.nanodegree.kt.moviesapp.ui.activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.android.nanodegree.kt.moviesapp.R
import com.android.nanodegree.kt.moviesapp.data.model.Movie
import com.android.nanodegree.kt.moviesapp.data.model.QueryType
import com.android.nanodegree.kt.moviesapp.ui.base.BaseActivity
import com.android.nanodegree.kt.moviesapp.ui.fragment.HomeFragment
import com.android.nanodegree.kt.moviesapp.viewmodel.Error
import com.android.nanodegree.kt.moviesapp.viewmodel.Event
import com.android.nanodegree.kt.moviesapp.viewmodel.activity.home.HomeActivityViewModel

class HomeActivity :
        BaseActivity<HomeActivityViewModel>(),
        HomeFragment.OnFragmentInteractionListener {

    companion object {

        private const val KEY_QUERY_TYPE = "key_query_type"
        private val DEFAULT_QUERY_TYPE = QueryType.MOST_POPULAR
    }

    private var _fragment: HomeFragment? = null

    private val fragment: HomeFragment
        get() {
            _fragment = _fragment ?:
                    supportFragmentManager.findFragmentByTag(HomeFragment.TAG)
                            as HomeFragment
            _fragment = _fragment ?:
                    HomeFragment.newInstance(viewModel.queryType.value)
            return _fragment as HomeFragment
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if (savedInstanceState?.containsKey(KEY_QUERY_TYPE)!! &&
                savedInstanceState.getSerializable(KEY_QUERY_TYPE) != null) {
            viewModel.queryType.postValue(
                    savedInstanceState.getSerializable(KEY_QUERY_TYPE) as QueryType
            )
        } else {
            viewModel.queryType.postValue(DEFAULT_QUERY_TYPE)
        }
        if (supportFragmentManager.findFragmentByTag(HomeFragment.TAG) == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragmentPlaceHolderLayout, fragment, HomeFragment.TAG)
                    .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putSerializable(KEY_QUERY_TYPE, viewModel.queryType.value)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (viewModel.queryType.value == DEFAULT_QUERY_TYPE) super.onBackPressed()
        else fragment.loadMovies(DEFAULT_QUERY_TYPE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_most_popular -> fragment.loadMovies(QueryType.MOST_POPULAR)
            R.id.action_top_rated -> fragment.loadMovies(QueryType.TOP_RATED)
            R.id.action_favorites -> fragment.loadMovies(QueryType.FAVORITES)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMovieClick(movie: Movie?): Unit = movie?.let {
        MovieDetailsActivity.startActivity(this, it)
    }!!

    override fun onQueryTypeChanged(newQueryType: QueryType) {
        viewModel.queryType.value = newQueryType
    }

    override fun getViewModelInstance() = HomeActivityViewModel.getInstance(this)

    override fun handleEvent(event: Event) {

    }

    override fun handleError(error: Error) {

    }

    override fun registerLiveDataObservers() {
        viewModel.queryType.observe(this, Observer { newQueryType ->
            newQueryType?.let { setTitle(newQueryType.resourceId) }
        })
    }
}