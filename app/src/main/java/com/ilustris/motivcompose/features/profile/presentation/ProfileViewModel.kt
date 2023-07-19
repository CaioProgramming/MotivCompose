package com.ilustris.motivcompose.features.profile.presentation

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ilustris.motiv.foundation.model.Cover
import com.ilustris.motiv.foundation.model.Icon
import com.ilustris.motiv.foundation.model.Quote
import com.ilustris.motiv.foundation.model.QuoteDataModel
import com.ilustris.motiv.foundation.model.User
import com.ilustris.motiv.foundation.service.QuoteHelper
import com.ilustris.motiv.foundation.service.QuoteService
import com.ilustris.motiv.foundation.service.UserService
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.delayedFunction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val quoteHelper: QuoteHelper,
    private val quoteService: QuoteService,
    override val service: UserService
) :
    BaseViewModel<User>(application) {

    val user = MutableLiveData<User>(null)
    val userQuotes = mutableStateListOf<QuoteDataModel>()
    val userFavorites = mutableStateListOf<QuoteDataModel>()
    val postsCount = MutableLiveData(0)
    val favoriteCount = MutableLiveData(0)
    val isOwnUser = MutableLiveData(false)


    fun fetchUser(uid: String? = service.currentUser()?.uid) {
        viewModelState.postValue(ViewModelBaseState.LoadingState)
        viewModelScope.launch(Dispatchers.IO) {
            val id = if (uid.isNullOrBlank()) service.currentUser()?.uid else uid
            id?.let {
                val userRequest = service.getSingleData(it)
                if (userRequest.isSuccess) {
                    user.postValue(userRequest.success.data as User)
                    isOwnUser.postValue(it == service.currentUser()?.uid)
                } else {
                    sendErrorState(userRequest.error.errorException)
                }
            } ?: run {
                Log.e(javaClass.simpleName, "fetchUser: no user logged")
            }
        }
    }

    fun getUserQuotes(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            quoteService.query(uid, "userID").run {
                if (isSuccess) {
                    val quotes = (success.data as List<Quote>).sortedByDescending { it.data }
                    postsCount.postValue(quotes.size)
                    loadQuoteListExtras(quotes)
                } else {
                    sendErrorState(error.errorException)
                }
            }
        }
    }

    fun getUserFavorites(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            quoteService.getFavorites(uid).run {
                if (isSuccess) {
                    val favoriteQuotes =
                        (success.data as List<Quote>).sortedByDescending { it.data }
                    favoriteCount.postValue(favoriteQuotes.size)
                    loadQuoteListExtras(favoriteQuotes, true)
                } else {
                    sendErrorState(error.errorException)
                }
            }
        }
    }

    private suspend fun loadQuoteListExtras(
        quotesDataList: List<Quote>,
        isFavorite: Boolean = false
    ) {
        quotesDataList.map {
            quoteHelper.mapQuoteToQuoteDataModel(it).run {
                if (this.isSuccess) {
                    if (!isFavorite) {
                        userQuotes.add(success.data)
                    } else {
                        userFavorites.add(success.data)
                    }
                } else {
                    sendErrorState(error.errorException)
                }
            }
        }
    }

    fun updateUserIcon(icon: Icon) {
        viewModelState.postValue(ViewModelBaseState.LoadingState)
        viewModelScope.launch(Dispatchers.IO) {
            service.editField(icon.uri, service.currentUser()?.uid ?: "", "picurl").run {
                if (isSuccess) {
                    refreshUser()
                } else {
                    sendErrorState(error.errorException)
                }
            }
        }
    }

    private fun refreshUser() {
        viewModelState.postValue(ViewModelBaseState.LoadingState)
        user.postValue(null)
        delayedFunction(500) {
            fetchUser()
        }
    }

    fun updateUserCover(cover: Cover) {
        viewModelState.postValue(ViewModelBaseState.LoadingState)
        viewModelScope.launch(Dispatchers.IO) {
            service.editField(cover.url, service.currentUser()?.uid ?: "", "cover").run {
                if (isSuccess) {
                    refreshUser()
                } else {
                    sendErrorState(error.errorException)
                }
            }
        }
    }

}