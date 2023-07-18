package com.ilustris.motivcompose.features.home.presentation

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ilustris.motiv.foundation.model.Quote
import com.ilustris.motiv.foundation.model.QuoteDataModel
import com.ilustris.motiv.foundation.model.Radio
import com.ilustris.motiv.foundation.model.Style
import com.ilustris.motiv.foundation.model.User
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
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    override val service: QuoteService,
    private val quoteHelper: QuoteHelper
) : BaseViewModel<Quote>(application) {

    val playingRadio = MutableLiveData<Radio>(null)

    val quotes = mutableStateListOf<QuoteDataModel>()
    var dataQuotes: List<Quote> = emptyList()
    var indexLimit = 10

    fun updatePlayingRadio(radio: Radio?) {
        playingRadio.postValue(radio)
    }

    override fun getAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            updateViewState(ViewModelBaseState.LoadingState)
            val result = service.getAllData(orderBy = "data")
            if (result.isSuccess) {
                val list = result.success.data as List<Quote>
                dataQuotes = list
                loadQuoteListExtras(list)
            } else sendErrorState(result.error.errorException)
        }
    }

    private fun loadQuoteListExtras(quotesDataList: List<Quote>) {
        viewModelScope.launch(Dispatchers.IO) {
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
    }

    fun loadMoreQuotes(startIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (indexLimit == dataQuotes.size) {
                Log.w(javaClass.simpleName, "loadMoreQuotes: limit reached")
                return@launch
            }
            dataQuotes.subList(startIndex, startIndex + indexLimit).forEach {
                val quoteModel = quoteHelper.mapQuoteToQuoteDataModel(it)
                if (quoteModel.isSuccess) {
                    quotes.add(quoteModel.success.data)
                }
            }
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
}
