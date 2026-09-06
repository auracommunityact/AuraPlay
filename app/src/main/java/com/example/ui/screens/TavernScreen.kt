package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TavernScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tavern", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AuraBackground)
            )
        },
        containerColor = AuraBackground
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Gaming Community Discussions", color = AuraTextSecondary)
        }
    }
}
