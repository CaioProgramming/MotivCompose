package com.ilustris.motiv.foundation.service

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.ilustris.motiv.foundation.model.AnimationProperties
import com.ilustris.motiv.foundation.model.Quote
import com.ilustris.motiv.foundation.model.QuoteDataModel
import com.ilustris.motiv.foundation.model.ShadowStyle
import com.ilustris.motiv.foundation.model.Style
import com.ilustris.motiv.foundation.model.StyleProperties
import com.ilustris.motiv.foundation.model.TextAlignment
import com.ilustris.motiv.foundation.model.TextProperties
import com.ilustris.motiv.foundation.model.User
import com.ilustris.motiv.foundation.model.Window
import com.ilustris.motiv.foundation.model.quoteList
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class QuoteHelper @Inject constructor(
    private val userService: UserService,
    private val styleService: StyleService
) {

    private val fallbackStyle = Style(
        backgroundURL = "https://media.giphy.com/media/5vgHoMiknf5iJl8FH1/giphy.gif",
        animationProperties = AnimationProperties(
            animation = com.ilustris.motiv.foundation.model.AnimationOptions.TYPE,
            transition = com.ilustris.motiv.foundation.model.AnimationTransition.LETTERS
        ),
        shadowStyle = ShadowStyle(
            radius = 0f,
            dx = 0f,
            dy = 0f,
            shadowColor = "#000000",
        ),
        textProperties = TextProperties(
            textColor = "#ffffff",
            textAlignment = TextAlignment.CENTER,
            fontStyle = com.ilustris.motiv.foundation.model.FontStyle.REGULAR,
            fontFamily = "Roboto"
        ),
        styleProperties = StyleProperties(
            backgroundColor = "#000000",
            customWindow = Window.MODERN
        )
    )

    suspend fun mapQuoteToQuoteDataModel(
        quote: Quote,
        isManager: Boolean = false
    ): ServiceResult<DataException, QuoteDataModel> {
        return try {
            val uid = userService.currentUser()?.uid
            val user = try {
                userService.getSingleData(quote.userID).success.data as User
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            val style = try {
                styleService.getSingleData(quote.style).success.data as Style
            } catch (e: Exception) {
                e.printStackTrace()
                fallbackStyle
            }

            ServiceResult.Success(
                QuoteDataModel(
                    quote,
                    user,
                    style,
                    isFavorite = quote.likes.contains(uid),
                    isUserQuote = quote.userID == uid || isManager
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ServiceResult.Error(
                DataException.UNKNOWN
            )
        }
    }

    suspend fun mapQuoteToQuoteDataModel(
        quotes: List<Quote>,
        isManager: Boolean = false
    ): ServiceResult<DataException, List<QuoteDataModel>> {

        return try {
            if (quotes.isEmpty()) {
                return ServiceResult.Error(DataException.NOTFOUND)
            }
            val quoteModels = quotes.map { quote ->
                val user = try {
                    userService.getSingleData(quote.userID).success.data as User
                } catch (e: Exception) {
                    null
                }
                val style = try {
                    styleService.getSingleData(quote.style).success.data as Style
                } catch (e: Exception) {
                    null
                }
                QuoteDataModel(
                    quote,
                    user,
                    style,
                    isFavorite = quote.likes.contains(user?.uid),
                    isUserQuote = quote.userID == userService.currentUser()?.uid || isManager
                )
            }
            ServiceResult.Success(quoteModels)
        } catch (e: Exception) {
            e.printStackTrace()
            ServiceResult.Error(DataException.UNKNOWN)
        }
    }

    suspend fun generateQuoteImage(
        context: Context,
        quote: Quote,
        bitmap: Bitmap
    ): ServiceResult<DataException, Uri> {
        return try {
            val shareFile = generateBitmapFile(quote, bitmap, context)
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                shareFile
            )
            ServiceResult.Success(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(javaClass.simpleName, "handleShare: Error generating file ${e.message}")
            ServiceResult.Error(DataException.UNKNOWN)
        }

    }

    private fun generateBitmapFile(quote: Quote, bitmap: Bitmap, context: Context): File {
        val cachePath = context.cacheDir.path + "/shared_quotes/"
        val cacheFile = File(cachePath)
        if (!cacheFile.exists()) {
            cacheFile.mkdirs()
        }
        val stream = FileOutputStream(cachePath + "motiv_${quote.id}.png")
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
        val file = File(cachePath + "motiv_${quote.id}.png")
        Log.i(javaClass.simpleName, "generateCardImage: file saved ${file.absolutePath}")
        return file
    }

}