package com.ilustris.motiv.foundation.service

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.ilustris.motiv.foundation.model.Quote
import com.ilustris.motiv.foundation.model.Radio
import com.silent.ilustriscore.core.bean.BaseBean
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.utilities.Ordering
import kotlinx.coroutines.tasks.await

class RadioService : BaseService() {
    override val dataPath: String = "Radios"
    override var requireAuth = true
    private fun storageReference() = FirebaseStorage.getInstance().reference.child(dataPath)

    override fun deserializeDataSnapshot(dataSnapshot: DocumentSnapshot): BaseBean? =
        dataSnapshot.toObject(Radio::class.java)?.apply {
            id = dataSnapshot.id
        }

    override fun deserializeDataSnapshot(dataSnapshot: QueryDocumentSnapshot): BaseBean =
        dataSnapshot.toObject(Radio::class.java).apply {
            id = dataSnapshot.id
        }

    override suspend fun getAllData(
        limit: Long,
        orderBy: String,
        ordering: Ordering
    ): ServiceResult<DataException, ArrayList<BaseBean>> {
        return super.getAllData(limit, "name", ordering)
    }

    override suspend fun addData(data: BaseBean): ServiceResult<DataException, BaseBean> {
        return try {
            val radio = data as Radio
            val uploadMusic =
                storageReference().child(radio.name).putFile(Uri.parse(radio.url)).await()
            return if (uploadMusic.task.isSuccessful) {
                radio.url = uploadMusic.storage.downloadUrl.await().toString()
                super.addData(radio)
            } else {
                ServiceResult.Error(DataException.UPLOAD)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ServiceResult.Error(DataException.SAVE)
        }

    }

    override suspend fun deleteData(id: String): ServiceResult<DataException, Boolean> {
        return try {
            val data = getSingleData(id).success.data as Radio
            storageReference().child(data.name).delete().await()
            return deleteData(id)
        } catch (e: Exception) {
            e.printStackTrace()
            ServiceResult.Error(DataException.DELETE)
        }
        return super.deleteData(id)
    }
}