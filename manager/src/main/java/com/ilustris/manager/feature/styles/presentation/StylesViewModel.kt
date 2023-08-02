package com.ilustris.manager.feature.styles.presentation

import android.app.Application
import com.ilustris.motiv.foundation.data.model.Style
import com.ilustris.motiv.foundation.service.StyleService
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StylesViewModel @Inject constructor(
    application: Application,
    override val service: StyleService
) : BaseViewModel<Style>(application) {
}