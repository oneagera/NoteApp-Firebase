package com.markus.noteapp_firebase.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.markus.noteapp_firebase.presentation.login.LoginViewModel
import com.markus.noteapp_firebase.presentation.common.Navigation
import com.markus.noteapp_firebase.ui.theme.NoteAppFirebaseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteAppFirebaseTheme {
                val loginViewModel: LoginViewModel = hiltViewModel()
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(
                        loginViewModel = loginViewModel,
                        navController = navController,
                        scope = scope,
                        drawerState = drawerState
                    )
                }
            }
        }
    }
}
