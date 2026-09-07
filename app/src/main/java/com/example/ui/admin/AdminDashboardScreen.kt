package com.example.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.auth.AuthState
import com.example.ui.auth.AuthViewModel
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraCard
import com.example.ui.theme.AuraPrimary
import com.example.ui.theme.AuraTextSecondary
import kotlinx.coroutines.launch
import java.io.InputStream
import com.example.data.SupabaseClient
import io.github.jan.supabase.storage.storage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onNavigateToGames: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    val profile = (authState as? AuthState.Authenticated)?.profile
    
    // Security check
    LaunchedEffect(profile) {
        if (profile?.role != "admin" && profile?.role != "super_admin") {
            onBack()
        }
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var uploadStatus by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            uploadStatus = "Uploading..."
            scope.launch {
                try {
                    val contentResolver = context.contentResolver
                    val mimeType = contentResolver.getType(it) ?: "image/jpeg"
                    val extension = if (mimeType.contains("png")) "png" else "jpg"
                    val inputStream: InputStream? = contentResolver.openInputStream(it)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {
                        val path = "admin_${profile?.id}/app-asset-${System.currentTimeMillis()}.$extension"
                        SupabaseClient.client.storage.from("app-assets").upload(path, bytes, upsert = true)
                        uploadStatus = "Upload Success: $path"
                        snackbarHostState.showSnackbar("Asset uploaded successfully")
                    }
                } catch (e: Exception) {
                    uploadStatus = "Upload failed: ${e.message}"
                    snackbarHostState.showSnackbar("Upload failed")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AuraBackground)
            )
        },
        containerColor = AuraBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Role: ${profile?.role?.uppercase()}", color = AuraPrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                AdminCard(
                    title = "Game Management",
                    description = "Add, edit, and publish games to the store.",
                    icon = Icons.Default.VideogameAsset,
                    onClick = { onNavigateToGames() }
                )
            }
            item {
                AdminCard(
                    title = "Media Manager",
                    description = "Upload images, banners, and app assets directly to Storage.",
                    icon = Icons.Default.CloudUpload,
                    onClick = {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                )
                if (uploadStatus.isNotEmpty()) {
                    Text(uploadStatus, color = AuraTextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }

            item {
                AdminCard(
                    title = "Home Layout Customization",
                    description = "Reorder sections, update featured games, and promotional banners.",
                    icon = Icons.Default.Dashboard,
                    onClick = { /* TODO */ }
                )
            }

            item {
                AdminCard(
                    title = "User Management",
                    description = "View users, manage roles, and suspend accounts.",
                    icon = Icons.Default.People,
                    onClick = { /* TODO */ }
                )
            }

            if (profile?.role == "super_admin") {
                item {
                    AdminCard(
                        title = "Super Admin Settings",
                        description = "Audit logs, admin assignments, and critical app config.",
                        icon = Icons.Default.Security,
                        onClick = { /* TODO */ }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = AuraCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(AuraBackground, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AuraPrimary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(description, color = AuraTextSecondary, fontSize = 14.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AuraTextSecondary)
        }
    }
}
