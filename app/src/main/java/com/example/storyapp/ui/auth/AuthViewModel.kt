package com.example.storyapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun isLogin() = authRepository.isLogin().asLiveData()

    fun login(email: String, password: String) = authRepository.login(email, password).asLiveData()

    fun register(name: String, email: String, password: String) = authRepository.register(name, email, password).asLiveData()

}