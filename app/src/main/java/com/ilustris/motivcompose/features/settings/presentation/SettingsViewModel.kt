package com.ilustris.motivcompose.features.settings.presentation

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ilustris.motiv.foundation.model.User
import com.ilustris.motiv.foundation.service.UserService
import com.silent.ilustriscore.core.model.BaseService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    override val service: UserService
) : BaseViewModel<User>(application) {


    val user = MutableLiveData<User>(null)

    fun fetchUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = service.currentUser()?.uid
            id?.let {
                val userRequest = service.getSingleData(it)
                if (userRequest.isSuccess) {
                    user.postValue(userRequest.success.data as User)
                } else {
                    sendErrorState(userRequest.error.errorException)
                }
            } ?: run {
                sendErrorState(DataException.AUTH)
                user.postValue(null)
            }
        }
    }

    fun deleteAccount() {
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
        fetchUser()
    }

    fun updateUserName(newName: String) {

    }

}