package com.ilustris.motiv.foundation.service

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.ilustris.motiv.foundation.data.model.Cover
import com.silent.ilustriscore.core.model.BaseService

class CoverService : BaseService() {
    override val dataPath = "Covers"

    override fun deserializeDataSnapshot(dataSnapshot: DocumentSnapshot): Cover {
        return dataSnapshot.toObject(Cover::class.java)!!.apply {
            this.id = dataSnapshot.id
        }
    }

    override fun deserializeDataSnapshot(dataSnapshot: QueryDocumentSnapshot): Cover {
        return dataSnapshot.toObject(Cover::class.java).apply {
            this.id = dataSnapshot.id
        }
    }

}