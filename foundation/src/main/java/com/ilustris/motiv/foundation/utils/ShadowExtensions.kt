package com.ilustris.motiv.foundation.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import com.ilustris.motiv.foundation.model.ShadowStyle

fun ShadowStyle.buildShadow() =
    Shadow(
        color = Color(android.graphics.Color.parseColor(shadowColor)),
        offset = Offset(dx, dy),
        blurRadius = radius
    )
