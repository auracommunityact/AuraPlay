package com.example.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.SupabaseClient
import com.example.data.models.Game
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraCard
import com.example.ui.theme.AuraPrimary
import com.example.ui.theme.AuraTextSecondary
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddEditGameScreen(
    gameId: String?,
    onBack: () -> Unit,
    viewModel: AdminGamesViewModel = viewModel()
) {
    val games by viewModel.games.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var game by remember { mutableStateOf(Game()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(games) {
        if (gameId != null && gameId != "new") {
            games.find { it.id == gameId }?.let { game = it }
        }
    }

    // Generic upload function
    fun uploadImage(uri: Uri, folder: String, onUrlReceived: (String) -> Unit) {
        isLoading = true
        scope.launch {
            try {
                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
                val extension = if (mimeType.contains("png")) "png" else "jpg"
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    val uniqueName = UUID.randomUUID().toString()
                    val path = "games/$folder/${uniqueName}.$extension"
                    SupabaseClient.client.storage.from("app-assets").upload(path, bytes, upsert = true)
                    val publicUrl = SupabaseClient.client.storage.from("app-assets").publicUrl(path)
                    onUrlReceived(publicUrl)
                }
            } catch (e: Exception) {
                // handle error
            } finally {
                isLoading = false
            }
        }
    }

    val iconLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { uploadImage(it, "icons") { url -> game = game.copy(icon_url = url) } }
    }

    val coverLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { uploadImage(it, "covers") { url -> game = game.copy(cover_url = url) } }
    }

    val screenshotLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
        uris.forEach { uri ->
            uploadImage(uri, "screenshots") { url ->
                val current = game.screenshots?.toMutableList() ?: mutableListOf()
                current.add(url)
                game = game.copy(screenshots = current)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (gameId == "new") "Add Game" else "Edit Game", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        scope.launch {
                            val success = viewModel.saveGame(game)
                            if (success) onBack()
                        }
                    }) {
                        Text("Save", color = AuraPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AuraBackground)
            )
        },
        containerColor = AuraBackground
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AuraPrimary)
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Text("Basic Information", color = AuraPrimary, fontWeight = FontWeight.Bold) }
            
            item {
                OutlinedTextField(
                    value = game.title,
                    onValueChange = { game = game.copy(title = it) },
                    label = { Text("Game Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }
            item {
                OutlinedTextField(
                    value = game.developer ?: "",
                    onValueChange = { game = game.copy(developer = it) },
                    label = { Text("Developer") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }
            item {
                OutlinedTextField(
                    value = game.publisher ?: "",
                    onValueChange = { game = game.copy(publisher = it) },
                    label = { Text("Publisher") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }
            item {
                OutlinedTextField(
                    value = game.short_description ?: "",
                    onValueChange = { game = game.copy(short_description = it) },
                    label = { Text("Short Description") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }
            item {
                OutlinedTextField(
                    value = game.genre ?: "",
                    onValueChange = { game = game.copy(genre = it) },
                    label = { Text("Category/Genre") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }
            item {
                OutlinedTextField(
                    value = game.description ?: "",
                    onValueChange = { game = game.copy(description = it) },
                    label = { Text("Full Description") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    maxLines = 5
                )
            }

            item {
                OutlinedTextField(
                    value = game.version ?: "",
                    onValueChange = { game = game.copy(version = it) },
                    label = { Text("Version") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }
            item {
                OutlinedTextField(
                    value = game.size ?: "",
                    onValueChange = { game = game.copy(size = it) },
                    label = { Text("Game Size (e.g. 2.4 GB)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }
            item { Text("Store Links", color = AuraPrimary, fontWeight = FontWeight.Bold) }
            item {
                OutlinedTextField(
                    value = game.google_play_url ?: "",
                    onValueChange = { game = game.copy(google_play_url = it) },
                    label = { Text("Google Play URL") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }
            item {
                OutlinedTextField(
                    value = game.apple_store_url ?: "",
                    onValueChange = { game = game.copy(apple_store_url = it) },
                    label = { Text("Apple App Store URL") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }

            item { Text("Media (From Device)", color = AuraPrimary, fontWeight = FontWeight.Bold) }
            
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { iconLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Upload Icon")
                    }
                    if (!game.icon_url.isNullOrEmpty()) {
                        AsyncImage(model = game.icon_url, contentDescription = "Icon", modifier = Modifier.size(48.dp))
                    }
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { coverLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Upload Cover")
                    }
                    if (!game.cover_url.isNullOrEmpty()) {
                        AsyncImage(model = game.cover_url, contentDescription = "Cover", modifier = Modifier.height(48.dp))
                    }
                }
            }

            item {
                Column {
                    Button(onClick = { screenshotLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Upload Screenshots (Max 5)")
                    }
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(game.screenshots ?: emptyList()) { url ->
                            AsyncImage(model = url, contentDescription = "Screenshot", modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
            
            item { Text("Settings", color = AuraPrimary, fontWeight = FontWeight.Bold) }
            item {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = game.status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("draft", "published", "hidden").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    game = game.copy(status = option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
