package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.theme.AppTheme
import com.example.ui.AuraPlayApp
import com.example.ui.auth.AuthState
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.LoginScreen

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authState by authViewModel.authState.collectAsState()

                    when (authState) {
                        is AuthState.Authenticated -> {
                            AuraPlayApp(authViewModel)
                        }
                        is AuthState.Loading -> {
                            // Professional Splash/Loading could go here, simple for now
                            Surface(color = MaterialTheme.colorScheme.background) {}
                        }
                        else -> {
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = {}
                            )
                        }
                    }
                }
            }
        }
    }
}
