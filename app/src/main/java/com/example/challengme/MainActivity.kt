package com.example.challengme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.challengme.res.navigation.MainNavGraph
import com.example.challengme.ui.res.values.ChallengMeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChallengMeTheme {
                MainNavGraph()
            }
        }
    }
}