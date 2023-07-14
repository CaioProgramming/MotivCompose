package com.ilustris.motivcompose.features.profile.presentation

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ilustris.motiv.foundation.model.Quote
import com.ilustris.motiv.foundation.model.QuoteDataModel
import com.ilustris.motiv.foundation.model.User
import com.ilustris.motiv.foundation.service.QuoteHelper
import com.ilustris.motiv.foundation.service.QuoteService
import com.ilustris.motiv.foundation.service.UserService
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.BaseViewModel
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
    val postsCount = MutableLiveData<Int>(0)
    val favoriteCount = MutableLiveData<Int>(0)


    fun fetchUser(uid: String? = service.currentUser()?.uid) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = if (uid.isNullOrBlank()) service.currentUser()?.uid else uid
            id?.let {
                val userRequest = service.getSingleData(it)
                if (userRequest.isSuccess) {
                    user.postValue(userRequest.success.data as User)
                    getUserQuotes(uid!!)
                    getUserFavorites(uid)
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
                    val quotes = (success.data as List<Quote>).sortedByDescending { it.data }
                    favoriteCount.postValue(quotes.size)
                    loadQuoteListExtras(quotes)
                } else {
                    sendErrorState(error.errorException)
                }
            }
        }
    }

    private fun loadQuoteListExtras(quotesDataList: List<Quote>) {
        viewModelScope.launch(Dispatchers.IO) {
            quotesDataList.forEach {
                quoteHelper.mapQuoteToQuoteDataModel(it).run {
                    if (isSuccess) {
                        userQuotes.add(success.data)
                    }
                }
            }

        }
    }

}