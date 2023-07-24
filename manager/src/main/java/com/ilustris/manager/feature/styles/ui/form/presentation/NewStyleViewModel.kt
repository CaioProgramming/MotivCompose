package com.ilustris.manager.feature.styles.ui.form.presentation

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.ilustris.motiv.foundation.model.ShadowStyle
import com.ilustris.motiv.foundation.model.Style
import com.ilustris.motiv.foundation.model.TextProperties
import com.ilustris.motiv.foundation.service.StyleService
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewStyleViewModel @Inject constructor(
    application: Application,
    override val service: StyleService
) : BaseViewModel<Style>(application) {

    val newStyle = MutableLiveData(Style())

    fun updateStyleColor(colorItem: String) {
        newStyle.postValue(newStyle.value?.copy(textColor = colorItem))
    }

    fun updateStyleBackColor(colorItem: String) {
        newStyle.postValue(
            newStyle.value?.copy(
                styleProperties = newStyle.value?.styleProperties?.copy(
                    backgroundColor = colorItem
                )
            )
        )
    }

    fun updateShadowStyle(shadowStyle: ShadowStyle) {
        newStyle.postValue(newStyle.value?.copy(shadowStyle = shadowStyle))
    }

    fun updateTextProperties(textProperties: TextProperties) {
        newStyle.postValue(newStyle.value?.copy(textProperties = textProperties))
    }

}