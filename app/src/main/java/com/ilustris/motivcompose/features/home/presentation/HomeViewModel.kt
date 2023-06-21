package com.ilustris.motivcompose.features.home.presentation

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ilustris.motiv.foundation.model.Quote
import com.ilustris.motiv.foundation.model.Radio
import com.ilustris.motiv.foundation.service.QuoteService
import com.ilustris.motiv.foundation.service.RadioService
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.ServiceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RadioState {
    data class RadioList(val radioList: List<Radio>) : RadioState()
    object Error : RadioState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    override val service: QuoteService,
    val radioService: RadioService
) : BaseViewModel<Quote>(application) {


    val radioState = MutableLiveData<RadioState>()


    fun getRadios() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = radioService.getAllData()
            if (result is ServiceResult.Success) {
                radioState.postValue(RadioState.RadioList(result.data as List<Radio>))
            } else {
                radioState.postValue(RadioState.Error)
            }
        }
    }
}