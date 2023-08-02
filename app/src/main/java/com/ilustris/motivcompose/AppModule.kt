package com.ilustris.motivcompose

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.ilustris.motiv.foundation.service.CoverService
import com.ilustris.motiv.foundation.service.QuoteHelper
import com.ilustris.motiv.foundation.service.IconService
import com.ilustris.motiv.foundation.service.PreferencesService
import com.ilustris.motiv.foundation.service.QuoteService
import com.ilustris.motiv.foundation.service.RadioService
import com.ilustris.motiv.foundation.service.StyleService
import com.ilustris.motiv.foundation.service.UserService
import com.ilustris.motiv.foundation.utils.RadioHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {


    @Singleton
    @Provides
    fun providesPreferencesService(@ApplicationContext context: Context) =
        PreferencesService(context)

    @Provides
    fun providesRadioHelper(
        @ApplicationContext context: Context,
        preferencesService: PreferencesService
    ) = RadioHelper(context, preferencesService)

    @Provides
    fun providesQuoteService() = QuoteService()

    @Provides
    fun providesRadioService(preferencesService: PreferencesService, radioHelper: RadioHelper) =
        RadioService(preferencesService, radioHelper)

    @Provides
    fun providesUserService() = UserService()

    @Provides
    fun providesIconService() = IconService()

    @Provides
    fun providesCoverService() = CoverService()

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