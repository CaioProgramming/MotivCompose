package com.ilustris.motiv.foundation.service

import android.net.Uri
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.ilustris.motiv.foundation.model.User
import com.silent.ilustriscore.core.bean.BaseBean
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import kotlinx.coroutines.tasks.await

class UserService : BaseService() {
    override val dataPath: String = "Users"

    override fun deserializeDataSnapshot(dataSnapshot: DocumentSnapshot): User? =
        dataSnapshot.toObject(
            User::class.java
        )

    override fun deserializeDataSnapshot(dataSnapshot: QueryDocumentSnapshot): User =
        dataSnapshot.toObject(
            User::class.java
        )

    suspend fun saveUser(icon: String): ServiceResult<DataException, BaseBean> {
        currentUser()!!.run {
            val profileChangeRequest =
                UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(icon)).build()
            updateProfile(profileChangeRequest).await()
            val newUser = User(
                uid = uid,
                picurl = icon,
                name = displayName ?: "",
            )
            return addData(newUser)
        }
    }
}