package com.markus.noteapp_firebase.presentation.login

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markus.noteapp_firebase.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    val hasUser: Boolean
        get() = repository.hasUser()

    var loginUiState by mutableStateOf(LoginUiState())
        private set

    fun onUserNameChange(userName: String) {
        loginUiState = loginUiState.copy(userName = userName)
    }

    fun onPasswordChange(password: String) {
        loginUiState = loginUiState.copy(password = password)
    }

    fun onUserNameChangeSignUp(userName: String) {
        loginUiState = loginUiState.copy(userNameSignUp = userName)
    }

    fun onPasswordChangeSignUp(password: String) {
        loginUiState = loginUiState.copy(passwordSignUp = password)
    }

    fun onConfirmPasswordChange(password: String) {
        loginUiState = loginUiState.copy(confirmpasswordSignUp = password)
    }

    fun onNavigate() {
        loginUiState = loginUiState.copy(userName = "", password = "")
    }

    private fun validateLoginForm() =
        loginUiState.userName.isNotBlank() &&
                loginUiState.password.isNotBlank()

    private fun validateSignUpForm() =
        loginUiState.userNameSignUp.isNotBlank() &&
                loginUiState.passwordSignUp.isNotBlank() &&
                loginUiState.confirmpasswordSignUp.isNotBlank()

    fun createUser(context: Context) = viewModelScope.launch {
        try {
            if (!validateSignUpForm()) {
                throw IllegalArgumentException("email and password cannot be empty")
            }
            loginUiState = loginUiState.copy(isLoading = true)
            if (loginUiState.passwordSignUp != loginUiState.confirmpasswordSignUp) {
                throw IllegalArgumentException("password does not match")
            }
            loginUiState = loginUiState.copy(signUpError = null)
            repository.createUser(
                loginUiState.userNameSignUp,
                loginUiState.passwordSignUp
            ) { isSuccessful ->
                if (isSuccessful) {
                    Toast.makeText(
                        context,
                        "successful login",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = true)
                } else {
                    Toast.makeText(
                        context,
                        "failed login",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = false)
                }
            }
        } catch (e: Exception) {
            loginUiState = loginUiState.copy(signUpError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            loginUiState = loginUiState.copy(isLoading = false)
        }
    }

    //login usr
    fun loginUser(context: Context) = viewModelScope.launch {
        try {
            if (!validateLoginForm()) {
                throw IllegalArgumentException("email and password cannot be empty")
            }
            loginUiState = loginUiState.copy(isLoading = true)
            loginUiState = loginUiState.copy(loginError = null)
            repository.loginUser(
                loginUiState.userName,
                loginUiState.password
            ) { isSuccessful ->
                if (isSuccessful) {
                    Toast.makeText(
                        context,
                        "successful login",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUiState = loginUiState.copy(
                        isSuccessLogin = true,
                        userName = "",
                        password = ""
                    )
                } else {
                    Toast.makeText(
                        context,
                        "failed login",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = false)
                }
            }
        } catch (e: Exception) {
            loginUiState = loginUiState.copy(loginError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            loginUiState = loginUiState.copy(isLoading = false)
        }
    }
}