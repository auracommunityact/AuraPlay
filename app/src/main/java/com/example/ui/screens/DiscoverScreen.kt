package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Notifications

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen() {
    var selectedTab by remember { mutableStateOf("Discover") }
    val tabs = listOf("Discover", "Top Charts", "Calendar", "Gamelist")

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
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
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
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = tabs.indexOf(selectedTab),
                containerColor = AuraBackground,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    if (tabs.indexOf(selectedTab) < tabPositions.size) {
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[tabs.indexOf(selectedTab)]),
                            color = AuraPrimary
                        )
                    }
                },
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
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

            // Featured Game
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    FeaturedGameCard()
                }
                item {
                    Text(
                        "Trending Games",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(5) { index ->
                    GameListCard(index = index)
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
fun FeaturedGameCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AuraCard)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Placeholder for image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2C2C3E))
            ) {
                Text(
                    "Game Cover Image",
                    color = AuraTextSecondary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, AuraBackground.copy(alpha = 0.9f)),
                            startY = 100f
                        )
                    )
            )
            // Content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text("Game of the Day", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Text("Aura Legends", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("★ 9.8", color = AuraPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(" • RPG • Adventure", color = AuraTextSecondary, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}

@Composable
fun GameListCard(index: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon placeholder
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(AuraCard, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Icon", color = AuraTextSecondary, fontSize = 12.sp)
        }
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text("Trending Game ${index + 1}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text("★ 8.${9 - index} • Action • Android", color = AuraTextSecondary, fontSize = 12.sp)
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
