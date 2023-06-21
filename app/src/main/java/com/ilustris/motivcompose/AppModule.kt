package com.ilustris.motivcompose

import com.ilustris.motiv.foundation.service.QuoteService
import com.ilustris.motiv.foundation.service.RadioService
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

}