package com.ilustris.motivcompose.features.home.presentation

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.BuildConfig
import com.google.firebase.Timestamp
import com.ilustris.motiv.foundation.data.model.Quote
import com.ilustris.motiv.foundation.data.model.QuoteDataModel
import com.ilustris.motiv.foundation.data.model.Radio
import com.ilustris.motiv.foundation.data.model.Report
import com.ilustris.motiv.foundation.data.model.Style
import com.ilustris.motiv.foundation.data.model.User
import com.ilustris.motiv.foundation.service.QuoteHelper
import com.ilustris.motiv.foundation.service.QuoteService
import com.ilustris.motiv.foundation.service.RadioService
import com.ilustris.motiv.foundation.service.StyleService
import com.ilustris.motiv.foundation.service.UserService
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.model.ViewModelBaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    override val service: QuoteService,
    private val quoteHelper: QuoteHelper
) : BaseViewModel<Quote>(application) {



    val quotes = mutableStateListOf<QuoteDataModel>()
    var dataQuotes: List<Quote> = emptyList()
    var shareState = MutableLiveData<ShareState>(null)


    override fun getAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            updateViewState(ViewModelBaseState.LoadingState)
            quotes.clear()
            val result = service.getAllData(orderBy = "data")
            if (result.isSuccess) {
                val list = result.success.data as List<Quote>
                dataQuotes = list
                loadQuoteListExtras(list)
            } else sendErrorState(result.error.errorException)
        }
    }

    private suspend fun loadQuoteListExtras(quotesDataList: List<Quote>) {
        try {
            quotes.clear()
            quotesDataList.forEach {
                quoteHelper.mapQuoteToQuoteDataModel(it).run {
                    if (isSuccess) {
                        quotes.add(success.data)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            sendErrorState(DataException.UNKNOWN)
        }
    }


    fun searchQuote(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (query.isNotEmpty()) {
                val queryQuotes = dataQuotes.filter {
                    it.quote.contains(query, true) || it.author.contains(query, true)
                }
                loadQuoteListExtras(queryQuotes)
            }
        }
    }

    fun deleteQuote(quoteDataModel: QuoteDataModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = service.deleteData(quoteDataModel.quoteBean.id)
            if (result.isSuccess) {
                quotes.remove(quoteDataModel)
            } else {
                sendErrorState(result.error.errorException)
            }
        }
    }

    fun likeQuote(quoteDataModel: QuoteDataModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val quote = quoteDataModel.quoteBean
            getUser()?.let {
                if (quote.likes.contains(it.uid)) {
                    quote.likes.remove(it.uid)
                } else {
                    quote.likes.add(it.uid)
                }
                val result = service.editData(quote)
                if (result.isSuccess) {
                    quotes[quotes.indexOf(quoteDataModel)] = quoteDataModel.copy(
                        quoteBean = quote,
                        isFavorite = quote.likes.contains(it.uid)
                    )
                } else {
                    sendErrorState(result.error.errorException)
                }
            }
        }
    }

    fun reportQuote(quote: Quote, reason: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getUser()?.let {
                quote.reports.add(Report(it.uid, reason, Timestamp.now()))
                super.editData(quote)
            }
        }
    }

    fun handleShare(quote: Quote, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            quoteHelper.generateQuoteImage(
                getApplication<Application>().applicationContext,
                quote,
                bitmap
            ).run {
                if (isSuccess) {
                    shareState.postValue(ShareState.ShareSuccess(this.success.data, quote))
                } else {
                    shareState.postValue(ShareState.ShareError)
                }
            }
        }
    }
}

sealed class ShareState {
    object ShareError : ShareState()
    data class ShareSuccess(val uri: Uri, val quote: Quote) : ShareState()
}