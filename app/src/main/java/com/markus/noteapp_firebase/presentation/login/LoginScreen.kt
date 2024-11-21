package com.markus.noteapp_firebase.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHomePage: () -> Unit,
    onNavigateToSignUpPage: () -> Unit
) {
    val loginUiState = loginViewModel.loginUiState
    val isError = loginUiState.loginError != null
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        if (isError) {
            Text(
                text = loginUiState.loginError ?: "unknown error",
                color = Color.Red
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = loginUiState.userName,
            onValueChange = { loginViewModel.onUserNameChange(it) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            label = {
                Text(text = "Email")
            },
            isError = isError
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = loginUiState.password,
            onValueChange = { loginViewModel.onPasswordChange(it) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            label = {
                Text(text = "Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = isError
        )

        Button(onClick = { loginViewModel.loginUser(context) }) {
            Text(text = "Sign In")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an account?")
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(
                onClick = {
                    onNavigateToSignUpPage.invoke()
                    loginViewModel.onNavigate()
                }
            ) {
                Text(text = "Sign Up")
            }
        }
        if (loginUiState.isLoading) {
            CircularProgressIndicator()
        }
        LaunchedEffect(
            key1 = loginViewModel.hasUser
        ) {
            if (loginViewModel.hasUser) {
                onNavigateToHomePage.invoke()
            }
        }
    }
}