package com.example.istandroid.nav


sealed class Screens(val route:String) {
    data object RegisterScreen: Screens(route = "register_screen")
    data object LoginScreen: Screens(route = "login_screen")
}

