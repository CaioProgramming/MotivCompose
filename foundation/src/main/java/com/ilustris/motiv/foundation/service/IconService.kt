package com.ilustris.motiv.foundation.service

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.ilustris.motiv.foundation.model.Icon
import com.silent.ilustriscore.core.bean.BaseBean
import com.silent.ilustriscore.core.model.BaseService

class IconService() : BaseService() {
    override val dataPath: String = "Icons"

    override fun deserializeDataSnapshot(dataSnapshot: DocumentSnapshot): Icon? =
        dataSnapshot.toObject(Icon::class.java)

    override fun deserializeDataSnapshot(dataSnapshot: QueryDocumentSnapshot): Icon =
        dataSnapshot.toObject(Icon::class.java)
}