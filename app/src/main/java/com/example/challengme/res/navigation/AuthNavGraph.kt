package com.example.challengme.res.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.challengme.data.remote.dto.routes.constant.AuthRoutes
import com.example.challengme.ui.auth.LoginScreen
import com.example.challengme.ui.auth.RecuperarPasswordScreen
import com.example.challengme.ui.auth.RegisterScreen
import com.example.challengme.ui.auth.WelcomeScreen


@Composable
fun AuthNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController    = navController,
        startDestination = AuthRoutes.WELCOME
    ) {
        composable(AuthRoutes.WELCOME) {
            WelcomeScreen(navController)
        }
        composable(AuthRoutes.LOGIN) {
            LoginScreen(navController)
        }
        composable(AuthRoutes.REGISTER) {
            RegisterScreen(navController)
        }
        composable(AuthRoutes.RECUPERAR_PASSWORD) {
            RecuperarPasswordScreen(navController)
        }
    }
}
