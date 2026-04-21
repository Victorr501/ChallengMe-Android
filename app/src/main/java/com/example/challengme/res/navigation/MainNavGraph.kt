package com.example.challengme.res.navigation

// ============================================================
//  MainNavGraph.kt
//  ChallengMe
//
//  Raíz de navegación. Decide qué mostrar según el estado
//  de autenticación — equivalente a ContentView.swift.
//
//  Uso en MainActivity:
//    setContent {
//        ChallengMeTheme {
//            MainNavGraph()
//        }
//    }
//
//  Requisito previo: AuthManager.init(context) debe haberse
//  llamado en Application.onCreate().
// ============================================================

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.challengme.data.local.AuthManager
import com.example.challengme.res.layout.MainLayout

@Composable
fun MainNavGraph() {
    val isAuthenticated by AuthManager.shared.isAuthenticated.collectAsState()

    // Sin animación de transición — cambio directo entre estados
    if (isAuthenticated) {
        MainLayout()
    } else {
        // AuthNavGraph gestiona su propio NavHostController internamente
        AuthNavGraph()
    }
}
