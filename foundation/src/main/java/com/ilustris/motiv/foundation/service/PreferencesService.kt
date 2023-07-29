package com.ilustris.motiv.foundation.service

import android.content.Context
import android.util.Log
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult


class PreferencesService(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("Sparky", Context.MODE_PRIVATE)

    private fun getEditor() = sharedPreferences.edit()

    fun editPreference(key: String, value: String): ServiceResult<DataException, String> {
        return try {
            getEditor().putString(key, value).commit()
            ServiceResult.Success("Preferências atualizadas")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(javaClass.simpleName, "editPreference: Error updating preferences ${e.message}")
            ServiceResult.Error(DataException.SAVE)
        }
    }

    fun getStringValue(key: String) = sharedPreferences.getString(key, null)


}