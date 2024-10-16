package com.markus.noteapp_firebase.presentation.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.markus.noteapp_firebase.presentation.detail.DetailScreen
import com.markus.noteapp_firebase.presentation.detail.DetailViewModel
import com.markus.noteapp_firebase.presentation.home.HomeScreen
import com.markus.noteapp_firebase.presentation.home.HomeViewModel
import com.markus.noteapp_firebase.presentation.login.LoginScreen
import com.markus.noteapp_firebase.presentation.login.LoginViewModel
import com.markus.noteapp_firebase.presentation.login.SignUpScreen

enum class LoginRoutes {
    SignUp,
    SignIn
}

enum class HomeRoutes {
    Home,
    Detail
}

enum class NestedRoutes {
    Main, //Home
    Login
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    detailViewModel: DetailViewModel,
    homeViewModel: HomeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = if (loginViewModel.hasUser) {
            NestedRoutes.Main.name
        } else {
            NestedRoutes.Login.name
        }

    ) {
        authGraph(navController, loginViewModel)
        homeGraph(
            navController = navController,
            detailViewModel,
            homeViewModel
        )
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel
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
                },
                loginViewModel = loginViewModel
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
                },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(LoginRoutes.SignIn.name)
            }
        }
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    detailViewModel: DetailViewModel,
    homeViewModel: HomeViewModel
) {
    navigation(
        startDestination = HomeRoutes.Home.name,
        route = NestedRoutes.Main.name
    ) {
        composable(HomeRoutes.Home.name) {
            HomeScreen(
                homeViewModel = homeViewModel,
                onNoteClick = { noteId ->
                    navController.navigate(
                        HomeRoutes.Detail.name + "?id=$noteId"
                    ) {
                        launchSingleTop = true
                    }
                },
                navigateToDetailPage = {
                    navController.navigate(HomeRoutes.Detail.name)
                }
            ) {
                navController.navigate(NestedRoutes.Login.name) {
                    launchSingleTop = true
                    popUpTo(0) {//remove everything from backstack
                        inclusive = true //including this(Login.name)
                    }
                }
            }
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
                detailViewModel = detailViewModel,
                noteId = entry.arguments?.getString("id") as String,
                onNavigate = { navController.navigate(HomeRoutes.Home.name) }
            )
        }
    }
}