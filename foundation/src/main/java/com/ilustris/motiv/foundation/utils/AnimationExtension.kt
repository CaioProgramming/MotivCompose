package com.ilustris.motiv.foundation.utils

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import com.ilustris.motiv.foundation.model.AnimationOptions


fun AnimationOptions.getEnterAnimation(): EnterTransition {
    return when (this) {
        AnimationOptions.TYPE -> fadeIn(tween(100))
        AnimationOptions.FADE -> fadeIn(tween(1500))
        AnimationOptions.SCALE -> scaleIn(tween(1000))
    }
}

fun AnimationOptions.getExitAnimation(): ExitTransition {
    return when (this) {
        AnimationOptions.TYPE -> fadeOut(tween(50))
        AnimationOptions.FADE -> fadeOut(tween(50))
        AnimationOptions.SCALE -> fadeOut(tween(50))
    }
}

fun AnimationOptions.getDelay(): Float {
    return when (this) {
        AnimationOptions.TYPE -> 100f
        AnimationOptions.FADE -> 500f
        AnimationOptions.SCALE -> 900f
    }
}