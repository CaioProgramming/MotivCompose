package com.ilustris.motiv.foundation.service

import com.ilustris.motiv.foundation.model.Quote
import com.ilustris.motiv.foundation.model.QuoteDataModel
import com.ilustris.motiv.foundation.model.Style
import com.ilustris.motiv.foundation.model.User
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import javax.inject.Inject

class QuoteHelper @Inject constructor(
    private val userService: UserService,
    private val styleService: StyleService
) {

    suspend fun mapQuoteToQuoteDataModel(quote: Quote): ServiceResult<DataException, QuoteDataModel> {
        return try {
            val uid = userService.currentUser()?.uid
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

            ServiceResult.Success(
                QuoteDataModel(
                    quote,
                    user,
                    style,
                    isFavorite = quote.likes.contains(user?.uid),
                    isUserQuote = quote.userID == uid
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ServiceResult.Error(
                DataException.UNKNOWN
            )
        }
    }

    suspend fun mapQuoteToQuoteDataModel(quotes: List<Quote>): ServiceResult<DataException, List<QuoteDataModel>> {

        return try {
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
                    isUserQuote = quote.userID == userService.currentUser()?.uid
                )
            }
            ServiceResult.Success(quoteModels)
        } catch (e: Exception) {
            e.printStackTrace()
            ServiceResult.Error(DataException.UNKNOWN)
        }
    }
}