package com.ilustris.motiv.foundation.utils

import android.content.Context
import android.util.Log
import com.ilustris.motiv.foundation.service.PreferencesService
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
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


    fun getRadioFile(radioId: String): File {
        val cachePath = getCacheDir()
        return File(cachePath, "$radioId.mp3")
    }

    fun saveRadioAudio(radioId: String, url: String): ServiceResult<DataException, Boolean> {
        try {
            val uriData = getUrlConnection(url)
            val file = getCacheFile(radioId)
            val fileOutputStream = FileOutputStream(file)
            val inputStream = uriData.getInputStream()
            val fileSize = uriData.contentLength
            val buffer = ByteArray(1024)

            repeat(inputStream.read(buffer)) {
                fileOutputStream.write(buffer, 0, it)
            }
            fileOutputStream.close()
            preferencesService.editPreference(radioId, file.path)
            return ServiceResult.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(javaClass.simpleName, "saveRadioAudio: Erro ao baixar rádio")
            return ServiceResult.Error(DataException.SAVE)
        }
    }

    private fun getCacheFile(radioId: String): File {
        val destination = getCacheDir()
        val fileName = "$radioId.mp3"
        return File(destination + fileName)
    }

    private fun getUrlConnection(url: String) = URL(url).openConnection().apply {
        doOutput = true
        connect()
    }

    private fun saveToCache() {

    }

}