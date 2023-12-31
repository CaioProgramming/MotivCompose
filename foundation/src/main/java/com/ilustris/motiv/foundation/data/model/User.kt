package com.ilustris.motiv.foundation.data.model

import com.google.firebase.auth.FirebaseUser
import com.silent.ilustriscore.core.bean.BaseBean


data class User(
    var name: String = "",
    var uid: String = "",
    var token: String = "",
    val admin: Boolean = false,
    var cover: String = DEFAULT_USER_BACKGROUND,
    var followers: ArrayList<User> = ArrayList(),
    var picurl: String = ""
) : BaseBean(uid) {


    companion object {

        val splashUser = User(
            name = "Ilustris",
            admin = true,
            picurl = "https://play-lh.googleusercontent.com/vhYXxYYP10iWVV5dUvS8gX3g1iG6N5kCAtCHD1UveJwYjfXAHLpzkVNMBS5FnfRVVQ=w144-h144-n-rw"
        )

        fun fromFirebase(firebaseUser: FirebaseUser): User {
            return User(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName ?: "Desconhecido",
                picurl = firebaseUser.photoUrl.toString()
            )
        }
    }
}
