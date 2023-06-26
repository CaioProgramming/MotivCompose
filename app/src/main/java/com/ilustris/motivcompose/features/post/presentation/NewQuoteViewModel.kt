package com.ilustris.motivcompose.features.post.presentation

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
    val styleService: StyleService
) : BaseViewModel<Quote>(application) {

    var newQuote = MutableLiveData(Quote())
    var currentStyle = MutableLiveData<Style?>(null)
    var styles = mutableStateListOf<Style>()
    fun updateQuoteText(text: String) {
        this.newQuote.postValue(newQuote.value?.apply { quote = text })
    }

    fun updateQuoteAuthor(authorText: String) {
        this.newQuote.postValue(newQuote.value?.apply { author = authorText })
    }

    fun updateStyle(styleId: String) {
        this.newQuote.postValue(newQuote.value?.apply { style = styleId })
        this.currentStyle.postValue(styles.find { it.id == styleId })
    }

    fun getStyles() {
        viewModelScope.launch(Dispatchers.IO) {
            styleService.getAllData(orderBy = "font").run {
                if (isSuccess) {
                    styles.addAll(success.data as List<Style>)
                    currentStyle.postValue(styles.random())
                } else {
                    sendErrorState(error.errorException)
                }
            }
        }
    }

    override fun saveData(quoteData: Quote) {
        quoteData.apply {
            data = Calendar.getInstance().time
            userID = getUser()?.uid ?: ""
            if (author.isEmpty()) {
                author = getUser()?.displayName ?: "Autor desconhecido"
            }
        }
        if (quoteData.quote.isNotEmpty() && quoteData.author.isNotEmpty()) {
            super.saveData(quoteData)
        }
    }

}