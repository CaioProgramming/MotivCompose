package com.ilustris.motivcompose.features.post.presentation

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.ilustris.motiv.foundation.model.Quote
import com.ilustris.motiv.foundation.model.Style
import com.ilustris.motiv.foundation.service.QuoteService
import com.ilustris.motiv.foundation.service.StyleService
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ViewModelBaseState
import com.silent.ilustriscore.core.utilities.delayedFunction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NewQuoteViewModel @Inject constructor(
    application: Application,
    override val service: QuoteService,
    private val styleService: StyleService
) : BaseViewModel<Quote>(application) {

    var newQuote = MutableLiveData(Quote())
    var currentStyle = MutableLiveData<Style?>(null)
    var styles = MutableLiveData<List<Style>>()


    fun updateQuoteText(text: String) {
        this.newQuote.postValue(newQuote.value?.copy(quote = text))
    }

    fun updateQuoteAuthor(authorText: String) {
        this.newQuote.postValue(newQuote.value?.copy(author = authorText))
    }

    fun updateStyle(styleId: String) {
        this.newQuote.postValue(newQuote.value?.copy(style = styleId))
        styles.value?.let {
            currentStyle.postValue(it.find { style -> style.id == styleId })
        }
    }

    fun getStyles(isPosted: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            styleService.getAllData(orderBy = "font").run {
                if (isSuccess) {
                    styles.postValue(success.data as List<Style>)
                } else {
                    sendErrorState(error.errorException)
                }
            }
        }
    }


    override fun saveData(data: Quote) {
        data.apply {
            if (this.data == null) this.data = Timestamp.now()
            userID = getUser()?.uid ?: ""
            if (author.isEmpty()) {
                author = getUser()?.displayName ?: "Autor desconhecido"
            }
        }
        if (data.quote.isNotEmpty() && data.author.isNotEmpty()) {
            if (data.id.isNotEmpty()) {
                editData(data)
            } else {
                super.saveData(data)
            }
        }
    }

    fun updateQuote(quote: Quote) {
        newQuote.postValue(quote)
        styles.value?.let {
            currentStyle.postValue(it.find { style -> style.id == quote.style })
        }
    }

}