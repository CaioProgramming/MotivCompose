package com.ilustris.motiv.foundation.service

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.ilustris.motiv.foundation.model.Quote
import com.ilustris.motiv.foundation.model.Radio
import com.silent.ilustriscore.core.bean.BaseBean
import com.silent.ilustriscore.core.model.BaseService

class RadioService : BaseService() {
    override val dataPath: String = "Radios"
    override var requireAuth = true

    override fun deserializeDataSnapshot(dataSnapshot: DocumentSnapshot): BaseBean? =
        dataSnapshot.toObject(Radio::class.java)

    override fun deserializeDataSnapshot(dataSnapshot: QueryDocumentSnapshot): BaseBean =
        dataSnapshot.toObject(Radio::class.java)
}