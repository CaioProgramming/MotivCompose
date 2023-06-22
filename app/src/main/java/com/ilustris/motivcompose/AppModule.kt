package com.ilustris.motivcompose

import com.firebase.ui.auth.AuthUI
import com.ilustris.motiv.foundation.service.IconService
import com.ilustris.motiv.foundation.service.QuoteService
import com.ilustris.motiv.foundation.service.RadioService
import com.ilustris.motiv.foundation.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun providesQuoteService() = QuoteService()

    @Provides
    fun providesRadioService() = RadioService()

    @Provides
    fun providesUserService() = UserService()

    @Provides
    fun providesIconService() = IconService()

    val loginProviders = listOf(
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.EmailBuilder().build()
    )

}