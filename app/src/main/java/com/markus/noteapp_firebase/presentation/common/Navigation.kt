package com.markus.noteapp_firebase.presentation.common

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.markus.noteapp_firebase.presentation.detail.DetailScreen
import com.markus.noteapp_firebase.presentation.home.HomeScreen
import com.markus.noteapp_firebase.presentation.login.LoginScreen
import com.markus.noteapp_firebase.presentation.login.LoginViewModel
import com.markus.noteapp_firebase.presentation.login.SignUpScreen
import com.markus.noteapp_firebase.presentation.trash.TrashScreen
import kotlinx.coroutines.CoroutineScope

enum class LoginRoutes {
    SignUp,
    SignIn
}

enum class HomeRoutes {
    Home,
    Detail,
    Trash
}

enum class NestedRoutes {
    Main, //Home
    Login
}

@Composable
fun Navigation(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    NavHost(
        navController = navController,
        startDestination = if (loginViewModel.hasUser) {
            NestedRoutes.Main.name
        } else {
            NestedRoutes.Login.name
        }

    ) {
        authGraph(navController)
        homeGraph(
            navController = navController,
            scope,
            drawerState
        )
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = LoginRoutes.SignIn.name,
        route = NestedRoutes.Login.name
    ) {
        composable(route = LoginRoutes.SignIn.name) {
            LoginScreen(
                onNavigateToHomePage = {
                    navController.navigate(NestedRoutes.Main.name) {
                        launchSingleTop =
                            true //create only one instance and not multiple in our backstack &/ app
                        popUpTo(route = LoginRoutes.SignIn.name) {
                            inclusive =
                                true //removes the signin page from backstack so that when usr presses back button on home page, it exits the app not go back to signin page
                        }
                    }
                }
            ) {
                navController.navigate(LoginRoutes.SignUp.name) {
                    launchSingleTop = true
                    popUpTo(LoginRoutes.SignIn.name) {
                        inclusive = true
                    }
                }
            }
        }

        composable(route = LoginRoutes.SignUp.name) {
            SignUpScreen(
                onNavigateToHomePage = {
                    navController.navigate(NestedRoutes.Main.name) {
                        popUpTo(LoginRoutes.SignUp.name) {
                            inclusive = true
                        }
                    }
                }
            ) {
                navController.navigate(LoginRoutes.SignIn.name)
            }
        }
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    navigation(
        startDestination = HomeRoutes.Home.name,
        route = NestedRoutes.Main.name
    ) {
        composable(HomeRoutes.Home.name) {
            HomeScreen(
                onNoteClick = { noteId ->
                    navController.navigate(
                        HomeRoutes.Detail.name + "?id=$noteId"
                    ) {
                        launchSingleTop = true
                    }
                },
                navigateToDetailPage = {
                    navController.navigate(HomeRoutes.Detail.name)
                },
                navigateToLoginPage = {
                    navController.navigate(NestedRoutes.Login.name) {
                        launchSingleTop = true
                        popUpTo(0) {//remove everything from backstack
                            inclusive = true //including this(Login.name)
                        }
                    }
                },
                scope = scope,
                drawerState = drawerState,
                navController = navController
            )
        }

        composable(
            route = HomeRoutes.Detail.name + "?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { entry ->

            DetailScreen(
                noteId = entry.arguments?.getString("id") as String,
                onNavigate = {
                    navController.navigate(HomeRoutes.Home.name) {
                        launchSingleTop =
                            true
                        popUpTo(route = HomeRoutes.Detail.name) {
                            inclusive =
                                true
                        }
                    }
                }
            )
        }

        composable(
            route = HomeRoutes.Trash.name
        ) {
            TrashScreen(
                navigateToLoginPage = {
                    navController.navigate(NestedRoutes.Login.name) {
                        launchSingleTop = true
                        popUpTo(0) {//remove everything from backstack
                            inclusive = true //including this(Login.name)
                        }
                    }
                },
                navController = navController,
                scope = scope,
                drawerState = drawerState
            )
        }
    }
}