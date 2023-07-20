package com.ilustris.motiv.foundation.ui.presentation

import android.app.Application
import com.ilustris.motiv.foundation.model.Icon
import com.ilustris.motiv.foundation.service.CoverService
import com.ilustris.motiv.foundation.service.IconService
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CoversViewModel @Inject constructor(
    application: Application,
    override val service: CoverService
) : BaseViewModel<Icon>(application) {
}