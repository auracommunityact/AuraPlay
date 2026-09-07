package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.TavernScreen
import com.example.ui.screens.MeScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.List

import com.example.ui.components.AuraPlayLogo

import com.example.ui.admin.AdminDashboardScreen

sealed class Screen(val route: String, val title: String, val icon: @Composable (isSelected: Boolean) -> Unit) {
    object Discover : Screen("discover", "Discovery", { Icon(Icons.Filled.Gamepad, contentDescription = "Discovery") })
    object Library : Screen("library", "Library", { Icon(Icons.Filled.List, contentDescription = "Library") })
    object Home : Screen("home", "", { AuraPlayLogo(Modifier.padding(bottom = 8.dp)) })
    object Tavern : Screen("tavern", "Social Tavern", { Icon(Icons.Filled.Forum, contentDescription = "Social Tavern") })
    object Me : Screen("me", "Me", { Icon(Icons.Filled.Person, contentDescription = "Me") })
    object Admin : Screen("admin", "Admin", { }) // Not in bottom bar
}

val items = listOf(
    Screen.Discover,
    Screen.Library,
    Screen.Home,
    Screen.Tavern,
    Screen.Me
)

@Composable
fun AuraPlayApp(authViewModel: com.example.ui.auth.AuthViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = AuraBackground,
                contentColor = AuraTextSecondary
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = { screen.icon(isSelected) },
                        label = if (screen.title.isNotEmpty()) {
                            { Text(screen.title) }
                        } else null,
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AuraPrimary,
                            unselectedIconColor = AuraTextSecondary,
                            selectedTextColor = AuraPrimary,
                            unselectedTextColor = AuraTextSecondary,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Discover.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Discover.route) { 
                DiscoverScreen(onGameClick = { gameId ->
                    navController.navigate("game_detail/$gameId")
                }) 
            }
            composable(Screen.Library.route) { LibraryScreen() }
            composable(Screen.Home.route) { 
                DiscoverScreen(onGameClick = { gameId ->
                    navController.navigate("game_detail/$gameId")
                }) 
            }
            composable(Screen.Tavern.route) { TavernScreen() }
            composable(Screen.Me.route) { 
                MeScreen(
                    authViewModel = authViewModel,
                    onNavigateToAdmin = { navController.navigate(Screen.Admin.route) }
                ) 
            }
            composable(Screen.Admin.route) {
                AdminDashboardScreen(
                    authViewModel = authViewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToGames = { navController.navigate("admin_games") }
                )
            }
            composable("admin_games") {
                com.example.ui.admin.AdminGamesScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToAddGame = { navController.navigate("admin_add_edit_game/new") },
                    onNavigateToEditGame = { gameId -> navController.navigate("admin_add_edit_game/$gameId") }
                )
            }
            composable(
                route = "admin_add_edit_game/{gameId}",
                arguments = listOf(androidx.navigation.navArgument("gameId") { type = androidx.navigation.NavType.StringType })
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getString("gameId")
                com.example.ui.admin.AdminAddEditGameScreen(
                    gameId = gameId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "game_detail/{gameId}",
                arguments = listOf(androidx.navigation.navArgument("gameId") { type = androidx.navigation.NavType.StringType })
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getString("gameId") ?: return@composable
                com.example.ui.screens.GameDetailScreen(
                    gameId = gameId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
