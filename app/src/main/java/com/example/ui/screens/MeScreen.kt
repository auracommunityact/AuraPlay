package com.example.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.auth.AuthState
import com.example.ui.auth.AuthViewModel
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraCard
import com.example.ui.theme.AuraPrimary
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen(authViewModel: AuthViewModel, onNavigateToAdmin: () -> Unit = {}) {
    val authState by authViewModel.authState.collectAsState()
    val profile = (authState as? AuthState.Authenticated)?.profile
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(it) ?: "image/jpeg"
            val extension = if (mimeType.contains("png")) "png" else "jpg"
            val inputStream: InputStream? = contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                authViewModel.uploadAvatar(bytes, extension)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
                actions = {
                    IconButton(onClick = { authViewModel.signOut() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Log Out", tint = AuraPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AuraBackground)
            )
        },
        containerColor = AuraBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(AuraCard)
                    .clickable {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (!profile?.avatar_url.isNullOrEmpty()) {
                    AsyncImage(
                        model = profile?.avatar_url,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = "Upload Avatar", tint = Color.White, modifier = Modifier.size(48.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = profile?.full_name ?: "Unknown User",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = profile?.role?.uppercase() ?: "USER",
                fontSize = 14.sp,
                color = AuraPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (profile?.role == "admin" || profile?.role == "super_admin") {
                Button(
                    onClick = onNavigateToAdmin,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AuraCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = AuraPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Admin Dashboard", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}
