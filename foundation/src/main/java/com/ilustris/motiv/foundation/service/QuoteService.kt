package com.ilustris.motiv.foundation.service

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.ilustris.motiv.foundation.model.Quote
import com.silent.ilustriscore.core.bean.BaseBean
import com.silent.ilustriscore.core.model.BaseService

class QuoteService : BaseService() {
    override val dataPath: String = "Quotes"
    override var requireAuth = true

    override fun deserializeDataSnapshot(dataSnapshot: DocumentSnapshot): BaseBean? =
        dataSnapshot.toObject(Quote::class.java)

    override fun deserializeDataSnapshot(dataSnapshot: QueryDocumentSnapshot): BaseBean =
        dataSnapshot.toObject(Quote::class.java)
}