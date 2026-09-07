package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraCard
import com.example.ui.theme.AuraPrimary
import com.example.ui.theme.AuraTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    gameId: String,
    onBack: () -> Unit,
    viewModel: GameDetailViewModel = viewModel()
) {
    val game by viewModel.game.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(gameId) {
        viewModel.fetchGameDetails(gameId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = "More", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(Color.Transparent)
            )
        },
        containerColor = AuraBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AuraPrimary)
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
            }
        } else if (game != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                // Hero Screenshots Pager
                item {
                    val images = game?.screenshots?.takeIf { it.isNotEmpty() } ?: listOf(game?.cover_url ?: "")
                    val pagerState = rememberPagerState(pageCount = { images.size })
                    
                    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            AsyncImage(
                                model = images[page],
                                contentDescription = "Screenshot $page",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        
                        // Pager Indicator
                        if (images.size > 1) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(images.size) { iteration ->
                                    val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                }
                            }
                        }
                    }
                }

                // Game Header Info
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(AuraCard)
                        ) {
                            if (!game?.icon_url.isNullOrEmpty()) {
                                AsyncImage(
                                    model = game?.icon_url,
                                    contentDescription = game?.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Titles and Developer
                        Column {
                            Text(
                                text = game?.title ?: "Unknown Title",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                            ) {
                                Icon(Icons.Default.Android, contentDescription = "Android", tint = AuraTextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.PhoneIphone, contentDescription = "Apple", tint = AuraTextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "•  ${game?.genre ?: "Unknown"}",
                                    color = AuraTextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "🏢 ${game?.developer ?: "Unknown Developer"}",
                                color = AuraTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Description
                item {
                    Text(
                        text = game?.description ?: "No description available for this game.",
                        color = AuraTextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Action Buttons
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AuraPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Download", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AuraCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Also available on Apple", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }

                // Icon Actions Row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ActionItem(Icons.Default.BookmarkBorder, "Wishlist")
                        ActionItem(Icons.Default.PlayArrow, "Played")
                        ActionItem(Icons.Default.Share, "Share")
                    }
                    Divider(color = AuraCard, modifier = Modifier.padding(vertical = 16.dp))
                }

                // Ratings Block
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Ratings & Reviews", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${game?.rating ?: "N/A"}",
                                    color = Color.White,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("out of 10", color = AuraTextSecondary, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(32.dp))

                            // Dummy rating bars
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                                RatingBarRow(stars = 5, fraction = 0.8f)
                                RatingBarRow(stars = 4, fraction = 0.4f)
                                RatingBarRow(stars = 3, fraction = 0.2f)
                                RatingBarRow(stars = 2, fraction = 0.1f)
                                RatingBarRow(stars = 1, fraction = 0.05f)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text("My Rating", color = Color.White, fontSize = 16.sp)
                        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(5) {
                                Icon(Icons.Default.StarBorder, contentDescription = null, tint = AuraTextSecondary, modifier = Modifier.size(32.dp))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "See All Reviews",
                            color = AuraPrimary,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally).clickable { }
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { }) {
        Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = AuraTextSecondary, fontSize = 14.sp)
    }
}

@Composable
fun RatingBarRow(stars: Int, fraction: Float) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.width(60.dp), horizontalArrangement = Arrangement.End) {
            repeat(stars) {
                Icon(Icons.Default.Star, contentDescription = null, tint = AuraTextSecondary, modifier = Modifier.size(10.dp))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .height(4.dp)
                .weight(1f)
                .background(AuraCard, RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .background(Color.White, RoundedCornerShape(2.dp))
            )
        }
    }
}
