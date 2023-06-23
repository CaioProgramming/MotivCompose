package com.ilustris.motiv.foundation.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.silent.ilustriscore.core.bean.BaseBean


const val DEFAULT_TEXT_COLOR = "#ffffff"
const val DEFAULT_SHADOW_COLOR = "#000000"
private const val DEFAULT_BACKGROUND_URL =
    "https://media.giphy.com/media/RJy4FQlLbxDz4kJ6GF/giphy.gif"


@IgnoreExtraProperties
data class Style(
    override var id: String = "",
    var font: Int = 0,
    var textAlignment: TextAlignment = TextAlignment.CENTER,
    var fontStyle: FontStyle = FontStyle.REGULAR,
    var textColor: String = DEFAULT_TEXT_COLOR,
    var backgroundURL: String = DEFAULT_BACKGROUND_URL,
    var shadowStyle: ShadowStyle = ShadowStyle(),
    @get:Exclude
    var storedStyle: Boolean = false
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

