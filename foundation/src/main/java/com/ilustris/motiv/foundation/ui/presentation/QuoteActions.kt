package com.ilustris.motiv.foundation.ui.presentation

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Rect
import com.ilustris.motiv.foundation.model.QuoteDataModel

interface QuoteActions {

    fun onClickUser(uid: String)
    fun onLike(dataModel: QuoteDataModel)
    fun onShare(dataModel: QuoteDataModel, bitmap: Bitmap)
    fun onDelete(dataModel: QuoteDataModel)
    fun onEdit(dataModel: QuoteDataModel)
    fun onReport(dataModel: QuoteDataModel)
}