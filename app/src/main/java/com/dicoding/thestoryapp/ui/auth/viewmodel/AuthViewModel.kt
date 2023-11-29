package com.dicoding.thestoryapp.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.thestoryapp.data.AuthRepository
import com.dicoding.thestoryapp.model.ResponseLogin
import com.dicoding.thestoryapp.model.ResponseGeneral
import com.dicoding.thestoryapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    fun login(email: String, password: String): LiveData<Result<ResponseLogin>> =
        authRepository.login(email, password)

    fun register(name: String, email: String, password: String): LiveData<Result<ResponseGeneral>> =
        authRepository.register(name, email, password)

}