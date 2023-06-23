package com.ilustris.motiv.foundation.model

import android.graphics.Typeface

data class QuoteDataModel(
    val quoteBean: Quote,
    val user: User?,
    val style: Style?,
    val typeface: Typeface? = null,
    val isFavorite: Boolean = false,
    val isUserQuote: Boolean = false
)
