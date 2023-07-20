package com.ilustris.motivcompose.features.settings.data

import java.util.Date

data class UserMetaData(
    val email: String? = "",
    val createTimeStamp: Long? = 0L,
    val provider: String? = "",
    val emailVerified: Boolean = false,
    val admin: Boolean = false
)
