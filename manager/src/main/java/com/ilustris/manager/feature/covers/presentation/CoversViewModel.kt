package com.ilustris.manager.feature.covers.presentation

import android.app.Application
import com.ilustris.motiv.foundation.data.model.Cover
import com.ilustris.motiv.foundation.service.CoverService
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CoversViewModel @Inject constructor(
    application: Application,
    override val service: CoverService
) : BaseViewModel<Cover>(application) {


}