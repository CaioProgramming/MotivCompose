package com.ilustris.motiv.foundation.ui.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilustris.motiv.foundation.service.QuoteHelper
import com.ilustris.motiv.foundation.model.Quote
import com.ilustris.motiv.foundation.model.QuoteDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(private val quoteHelper: QuoteHelper) : ViewModel() {

    val quoteModel = MutableLiveData<QuoteDataModel>(null)

    fun getQuoteExtras(quote: Quote) {
        viewModelScope.launch(Dispatchers.IO) {
            val model = quoteHelper.mapQuoteToQuoteDataModel(quote)
            if (model.isSuccess) {
                quoteModel.postValue(model.success.data)
            }
        }
    }

}