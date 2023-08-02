package com.ilustris.motiv.foundation.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ilustris.motiv.foundation.service.PreferencesService
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.net.URL
import javax.inject.Inject


class RadioHelper @Inject constructor(
    val context: Context,
    private val preferencesService: PreferencesService
) {

    private fun getCacheDir(): String {
        val cachePath = context.cacheDir.path + "/radios/"
        val cacheFile = File(cachePath)
        if (!cacheFile.exists()) {
            cacheFile.mkdirs()
        }
        return cachePath
    }


    fun createRadioFile(radioName: String): File {
        val cachePath = getCacheDir()
        return File(cachePath, "$radioName.mp3")
    }

    fun getRadioFile(url: String?): File? {
        return try {
            File(URI.create(url))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}