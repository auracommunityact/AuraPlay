package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.models.Game
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCard
import com.example.ui.theme.AuraPrimary
import com.example.ui.theme.AuraTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(onGameClick: (String) -> Unit = {}, viewModel: DiscoverViewModel = viewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedTab by remember { mutableStateOf("Games") }
    val tabs = listOf("Games", "Apps", "Offers")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        com.example.ui.components.AuraPlayLogo(modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "AURA PLAY",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuraBackground
                )
            )
        },
        containerColor = AuraBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search by title, developer, or genre...", color = AuraTextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = AuraTextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = AuraTextSecondary)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AuraCard,
                    unfocusedContainerColor = AuraCard,
                    focusedBorderColor = AuraPrimary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            if (searchQuery.isNotEmpty()) {
                // SHOW SEARCH RESULTS IN GRID
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AuraPrimary)
                    }
                } else if (error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                    }
                } else if (searchResults.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No games found.", color = AuraTextSecondary)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(150.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(searchResults) { game ->
                            GameGridCard(game = game, onClick = { onGameClick(game.id) })
                        }
                    }
                }
            } else {
                // SHOW REGULAR DISCOVER LAYOUT
                Column(modifier = Modifier.fillMaxSize()) {
                    // Tabs
                    ScrollableTabRow(
                        selectedTabIndex = tabs.indexOf(selectedTab),
                        containerColor = AuraBackground,
                        contentColor = Color.White,
                        indicator = { tabPositions ->
                            if (tabs.indexOf(selectedTab) < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[tabs.indexOf(selectedTab)]),
                                    color = AuraPrimary
                                )
                            }
                        },
                        edgePadding = 16.dp,
                        divider = {}
                    ) {
                        tabs.forEachIndexed { _, title ->
                            Tab(
                                selected = selectedTab == title,
                                onClick = { selectedTab = title },
                                text = {
                                    Text(
                                        title,
                                        fontWeight = if (selectedTab == title) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTab == title) Color.White else AuraTextSecondary
                                    )
                                }
                            )
                        }
                    }

                    // Category Chips
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = listOf("For You", "Editor's Choice", "Action", "Adventure", "RPG", "Strategy")
                        items(categories.size) { index ->
                            CategoryChip(categories[index], isSelected = index == 0)
                        }
                    }

                    // Discover Content (From DB)
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AuraPrimary)
                        }
                    } else if (searchResults.isNotEmpty()) {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    "Trending Games",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(searchResults.take(5).size) { index ->
                                GameListCard(game = searchResults[index], onClick = { onGameClick(searchResults[index].id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(label: String, isSelected: Boolean) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) AuraCard else Color.Transparent,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, AuraBorder) else null,
        modifier = Modifier.clickable { }
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else AuraTextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
fun GameListCard(game: Game, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(AuraCard, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (!game.icon_url.isNullOrEmpty()) {
                AsyncImage(
                    model = game.icon_url,
                    contentDescription = game.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(game.title.take(1), color = AuraTextSecondary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(game.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            Text("★ ${game.rating ?: "N/A"} • ${game.genre ?: "Unknown"}", color = AuraTextSecondary, fontSize = 12.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
        }
        
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = AuraCard,
                contentColor = AuraPrimary
            ),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text("Get", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GameGridCard(game: Game, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AuraCard)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFF2C2C3E))
            ) {
                if (!game.cover_url.isNullOrEmpty()) {
                    AsyncImage(
                        model = game.cover_url,
                        contentDescription = game.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = game.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = game.genre ?: game.developer ?: "Unknown",
                    color = AuraTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★ ${game.rating ?: "N/A"}",
                        color = AuraPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
