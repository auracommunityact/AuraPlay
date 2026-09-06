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
import com.example.ui.screens.PlayScreen
import com.example.ui.screens.TavernScreen
import com.example.ui.screens.MeScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Gamepad

import com.example.ui.components.AuraPlayLogo

import com.example.ui.admin.AdminDashboardScreen

sealed class Screen(val route: String, val title: String, val icon: @Composable (isSelected: Boolean) -> Unit) {
    object Discover : Screen("discover", "Games", { Icon(Icons.Filled.Gamepad, contentDescription = "Games") })
    object Play : Screen("play", "Play", { Icon(Icons.Filled.PlayArrow, contentDescription = "Play") })
    object Home : Screen("home", "", { AuraPlayLogo(Modifier.padding(bottom = 8.dp)) })
    object Tavern : Screen("tavern", "Tavern", { Icon(Icons.Filled.Forum, contentDescription = "Tavern") })
    object Me : Screen("me", "Me", { Icon(Icons.Filled.Person, contentDescription = "Me") })
    object Admin : Screen("admin", "Admin", { }) // Not in bottom bar
}

val items = listOf(
    Screen.Discover,
    Screen.Play,
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
            composable(Screen.Discover.route) { DiscoverScreen() }
            composable(Screen.Play.route) { PlayScreen() }
            composable(Screen.Home.route) { DiscoverScreen() }
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
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
