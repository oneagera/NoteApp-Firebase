package com.markus.noteapp_firebase.presentation.login

data class LoginUiState(
    val userName: String = "",
    val password: String = "",
    val userNameSignUp: String = "",
    val passwordSignUp: String = "",
    val confirmpasswordSignUp: String = "",
    val isLoading: Boolean = false,
    val isSuccessLogin: Boolean = false,
    val signUpError: String? = null, //null cannot be a value for non-null type 'String' so ?
    val loginError: String? = null
)
