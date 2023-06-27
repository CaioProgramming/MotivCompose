package com.ilustris.motivcompose

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.ilustris.motiv.foundation.model.Icon
import com.ilustris.motiv.foundation.model.User
import com.ilustris.motiv.foundation.service.IconService
import com.ilustris.motiv.foundation.service.UserService
import com.silent.ilustriscore.core.model.BaseViewModel
import com.silent.ilustriscore.core.model.DataException
import com.silent.ilustriscore.core.model.ServiceResult
import com.silent.ilustriscore.core.model.ViewModelBaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    override val service: UserService,
    private val iconService: IconService,
    application: Application
) : BaseViewModel<User>(
    application
) {

    val currentUser = MutableLiveData<User?>(null)

    fun validateAuth() {
        if (!isAuthenticated()) updateViewState(ViewModelBaseState.RequireAuth)
    }

    fun fetchUser() {
        viewModelScope.launch(Dispatchers.IO) {
            if (service.currentUser() == null) {
                updateViewState(ViewModelBaseState.RequireAuth)
                return@launch
            }
            val userRequest = service.getSingleData(service.currentUser()?.uid ?: "")
            if (userRequest is ServiceResult.Success) {
                currentUser.postValue(userRequest.data as User)
            } else {
                updateViewState(ViewModelBaseState.ErrorState(userRequest.error.errorException))
            }
        }
    }


    fun validateLogin(result: FirebaseAuthUIAuthenticationResult?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (result == null) {
                updateViewState(ViewModelBaseState.ErrorState(DataException.AUTH))
            } else {
                val userRequest = service.getSingleData(service.currentUser()?.uid ?: "")
                if (userRequest is ServiceResult.Success) {
                    currentUser.postValue(userRequest.data as User)
                } else {
                    val icons = iconService.getAllData()
                    if (icons is ServiceResult.Success) {
                        val iconList = icons.data as List<Icon>
                        val newUserResult = service.saveUser(iconList.random().uri)
                        if (newUserResult is ServiceResult.Success) {
                            currentUser.postValue(newUserResult.data as User)
                        } else {
                            updateViewState(ViewModelBaseState.ErrorState(newUserResult.error.errorException))
                        }
                    }
                }
            }
        }
    }


}