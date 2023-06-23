package com.ilustris.motivcompose

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.ilustris.motiv.foundation.service.QuoteHelper
import com.ilustris.motiv.foundation.service.IconService
import com.ilustris.motiv.foundation.service.QuoteService
import com.ilustris.motiv.foundation.service.RadioService
import com.ilustris.motiv.foundation.service.StyleService
import com.ilustris.motiv.foundation.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

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

    @Provides
    fun providesStyleService() = StyleService()

    @Provides
    fun providesQuoteHelper(userService: UserService, styleService: StyleService) =
        QuoteHelper(userService, styleService)

    val loginProviders = listOf(
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.EmailBuilder().build()
    )

}