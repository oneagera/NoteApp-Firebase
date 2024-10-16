package com.markus.noteapp_firebase.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markus.noteapp_firebase.presentation.detail.DetailViewModel
import com.markus.noteapp_firebase.presentation.home.HomeViewModel
import com.markus.noteapp_firebase.presentation.login.LoginViewModel
import com.markus.noteapp_firebase.presentation.util.Navigation
import com.markus.noteapp_firebase.ui.theme.NoteAppFirebaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteAppFirebaseTheme {
                val loginViewModel = viewModel(modelClass = LoginViewModel::class.java)
                val homeViewModel = viewModel(modelClass = HomeViewModel::class.java)
                val detailViewModel = viewModel(modelClass = DetailViewModel::class.java)
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(
                        loginViewModel = loginViewModel,
                        homeViewModel = homeViewModel,
                        detailViewModel = detailViewModel
                    )
                }
            }
        }
    }
}
