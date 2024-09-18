package com.example.istandroid.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.istandroid.screen.LoginScreen
import com.example.istandroid.screen.RegisterScreen


@Composable
fun NavGraph(
    navController: NavHostController
)
{
    NavHost(
        navController = navController,
        startDestination = Screens.RegisterScreen.route
    ){
//        register screen
        composable(
            route =  Screens.RegisterScreen.route
        ){
            RegisterScreen(navController = navController)
        }

//        login screen
        composable(
            route = Screens.LoginScreen.route
        ){
            LoginScreen(navController = navController)
        }
    }
}