package com.markus.noteapp_firebase.presentation.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.RestoreFromTrash
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawer(
    navController: NavController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    navigateToLoginPage: () -> Unit,
    content: @Composable () -> Unit

) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val items = listOf(
        NavigationItem(
            title = "Notes",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = HomeRoutes.Home.name,
            onClick = { navController.navigate(HomeRoutes.Home.name) }
        ),
        NavigationItem(
            title = "Trash",
            selectedIcon = Icons.Filled.RestoreFromTrash,
            unselectedIcon = Icons.Outlined.RestoreFromTrash,
            route = HomeRoutes.Trash.name,
            onClick = { navController.navigate(HomeRoutes.Trash.name) }
        ),
        NavigationItem(
            title = "Logout",
            selectedIcon = Icons.AutoMirrored.Filled.ExitToApp,
            unselectedIcon = Icons.AutoMirrored.Outlined.ExitToApp,
            route = NestedRoutes.Login.name,
            onClick = {
                navigateToLoginPage.invoke()
            }
        )
    )

    //Nesting selectedItemIndex with navigation so that the correct nav drawer item updates selection state as soon as we navigate
    val selectedItemIndex = items.indexOfFirst { it.route == currentRoute }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                items.forEachIndexed { index, navigationItem ->
                    NavigationDrawerItem(
                        label = { Text(text = navigationItem.title) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            if (currentRoute != navigationItem.route) {
                                navigationItem.onClick()
                                scope.launch {
                                    drawerState.close() //close our drawer sheet once an item is selected
                                }
                            } else {
                                scope.launch {
                                    drawerState.close() // Close the drawer even if the screen is already selected
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedItemIndex) {
                                    navigationItem.selectedIcon
                                } else navigationItem.unselectedIcon,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        drawerState = drawerState,
        content = content
    )
}