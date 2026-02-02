package com.example.notas.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notas.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun updateUsername(value: String) { _username.value = value }
    fun updateEmail(value: String) { _email.value = value }
    fun updatePassword(value: String) { _password.value = value }
    fun updateConfirmPassword(value: String) { _confirmPassword.value = value }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.login(_email.value, _password.value)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun signup(onSuccess: () -> Unit) {
        if (_password.value != _confirmPassword.value) {
            _error.value = "As senhas não coincidem"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.signup(_username.value, _email.value, _password.value)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}
