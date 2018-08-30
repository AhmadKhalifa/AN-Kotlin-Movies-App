package com.android.nanodegree.kt.moviesapp.viewmodel

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.support.annotation.StringRes
import com.android.nanodegree.kt.moviesapp.R
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Khalifa on 12/08/2018.
 *
 */

open class BaseRxViewModel : ViewModel() {

    val event: MutableLiveData<Event> = MutableLiveData()
    val error: MutableLiveData<Error> = MutableLiveData()

    private val compositeDisposable: CompositeDisposable  = CompositeDisposable()

    protected fun <T> performAsync(action: () -> T?,
                                   onSuccess: (T?) -> Unit = {},
                                   onFailure: (Throwable) -> Unit = {}) {
        compositeDisposable.add(
                Flowable.fromCallable<T> {
                    try {
                        action()
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        throw Exception(exception)
                    }
                }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onSuccess, onFailure)
        )
    }

    protected fun performAsync (action: () -> Unit) {
        performAsync(action, onSuccess = {}, onFailure = {})
    }

    protected fun notify(event: Event) {
        this.event.value = event
    }

    protected fun notify(error: Error) {
        this.error.value = error
    }

    fun clearDisposables() {
        compositeDisposable.clear()
    }
}

enum class Error (@StringRes val stringResId: Int) {
    GENERAL_ERROR(R.string.general_error),
    NO_INTERNET_CONNECTION(R.string.no_internet_connection),
    NON_STABLE_CONNECTION(R.string.non_stable_connection),
    ERROR_LOADING_MOVIES(R.string.error_loading_movies),
    ERROR_LOADING_FAVORITE_MOVIES(R.string.error_loading_favorite_movies),
    ERROR_LOADING_MOVIE_TRAILERS(R.string.error_loading_movie_trailers),
    ERROR_LOADING_MOVIE_REVIEWS(R.string.error_loading_movie_reviews),
    ERROR_ADDING_MOVIE_TO_FAVORITES(R.string.error_adding_movie_to_favorites),
    ERROR_REMOVING_MOVIE_FROM_FAVORITES(R.string.error_removing_movie_from_favorites),
    ERROR_GETTING_MOVIE_FAVORITE_STATE(R.string.error_getting_movie_favorite_state);
}

enum class Event(@StringRes val stringResId: Int) {
    ADDED_TO_FAVORITES(R.string.added_to_favorites),
    REMOVED_FROM_FAVORITES(R.string.removed_from_favorites)
}

interface BaseViewModelOwner<out VM : BaseRxViewModel> {

    fun registerEventHandlerSubscribers(lifecycleOwner: LifecycleOwner,
                                        viewModel: BaseRxViewModel) {
        viewModel.event.observe(
                lifecycleOwner,
                Observer { event -> event?.let { handleEvent(it) } }
        )
        viewModel.error.observe(
                lifecycleOwner,
                Observer { error -> error?.let { handleError(it) } }
        )
    }

    fun getViewModelInstance(): VM

    fun handleEvent(event: Event)

    fun handleError(error: Error)

    fun registerLiveDataObservers()
}