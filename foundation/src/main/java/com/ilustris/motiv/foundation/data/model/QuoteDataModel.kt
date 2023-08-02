package com.ilustris.motiv.foundation.data.model

import android.graphics.Typeface

data class QuoteDataModel(
    val quoteBean: Quote,
    val user: User?,
    val style: Style?,
    val isFavorite: Boolean = false,
    val isUserQuote: Boolean = false
)
