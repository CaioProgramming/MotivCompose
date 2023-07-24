package com.ilustris.motiv.foundation.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.silent.ilustriscore.core.bean.BaseBean


const val DEFAULT_TEXT_COLOR = "#ffffff"
const val DEFAULT_SHADOW_COLOR = "#000000"
private const val DEFAULT_BACKGROUND_URL =
    "https://media.giphy.com/media/RJy4FQlLbxDz4kJ6GF/giphy.gif"
const val NEW_STYLE_BACKGROUND = "https://media.giphy.com/media/bLdgTj2jCKe9Wf94Km/giphy.gif"


@IgnoreExtraProperties
data class Style(
    override var id: String = "",
    var font: Int = 0,
    var textAlignment: TextAlignment = TextAlignment.CENTER,
    var fontStyle: FontStyle = FontStyle.REGULAR,
    var textColor: String = DEFAULT_TEXT_COLOR,
    var backgroundURL: String = DEFAULT_BACKGROUND_URL,
    var shadowStyle: ShadowStyle = ShadowStyle(),
    var styleProperties: StyleProperties = StyleProperties(),
) : BaseBean(id) {

}

enum class TextAlignment {
    CENTER, START, END, JUSTIFY
}

enum class FontStyle {
    REGULAR, BOLD, ITALIC
}

data class ShadowStyle(
    var radius: Float = 0f,
    var dx: Float = 0f,
    var dy: Float = 0f,
    var shadowColor: String = DEFAULT_SHADOW_COLOR,
    var strokeColor: String = DEFAULT_SHADOW_COLOR
)

data class StyleProperties(
    val clipMask: Boolean = false,
    val backgroundColor: String? = null,
    val blendMode: BlendMode = BlendMode.NORMAL,
    val customWindow: Window = Window.MODERN,
    val animation: Animation = Animation.TYPE
)

enum class BlendMode {
    NORMAL, DARKEN, LIGHTEN, OVERLAY, SCREEN
}

enum class Animation {
    TYPE, FADE, SCALE
}

enum class Window {
    CLASSIC, MODERN, GRADIENT, DEPTH
}