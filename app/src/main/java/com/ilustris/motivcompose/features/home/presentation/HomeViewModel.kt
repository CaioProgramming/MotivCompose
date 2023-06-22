package com.ilustris.motivcompose.features.home.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ilustris.motiv.foundation.model.Quote
import com.ilustris.motiv.foundation.model.Radio
import com.ilustris.motiv.foundation.service.QuoteService
import com.ilustris.motiv.foundation.service.RadioService
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.BaseViewModel
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
) : BaseViewModel<Quote>(application) {

    val playingRadio = MutableLiveData<Radio>(null)

    fun updatePlayingRadio(radio: Radio?) {
        playingRadio.postValue(radio)
    }

    override fun getAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            updateViewState(ViewModelBaseState.LoadingState)
            val result = service.getAllData(orderBy = "data")
            if (result.isSuccess) {
                updateViewState(ViewModelBaseState.DataListRetrievedState(result.success.data))
            } else sendErrorState(result.error.errorException)
        }
    }

}