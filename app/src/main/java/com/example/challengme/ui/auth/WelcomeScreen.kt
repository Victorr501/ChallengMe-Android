package com.example.challengme.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.challengme.res.navigation.AuthRoutes
import com.example.challengme.ui.res.values.LocalShapes
import com.example.challengme.ui.res.values.LocalSpacing

@Composable
fun WelcomeScreen(navController: NavController) {
    val spacing = LocalSpacing.current
    val shapes  = LocalShapes.current
    val colors  = MaterialTheme.colorScheme
    val typo    = MaterialTheme.typography

    Box(
        modifier           = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment   = Alignment.Center
    ) {
        Column(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.xl),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.Center
        ) {
            Text(
                text  = buildAnnotatedString {
                    append("Challenge ")
                    withStyle(SpanStyle(color = colors.primary)) { append("M") }
                    append("e!")
                },
                style = typo.displayLarge,
                color = colors.onBackground
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            Text(
                text  = "Supera retos. Bate récords. Sé el mejor.",
                style = typo.bodySmall,
                color = colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(spacing.xxl))

            Button(
                onClick  = { navController.navigate(AuthRoutes.REGISTER) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = RoundedCornerShape(shapes.md),
                colors   = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Text(
                    text  = "Registrarse",
                    style = typo.titleMedium,
                    color = colors.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(spacing.md))

            OutlinedButton(
                onClick  = { navController.navigate(AuthRoutes.LOGIN) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = RoundedCornerShape(shapes.md),
                border   = BorderStroke(1.5.dp, colors.primary),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
            ) {
                Text(
                    text  = "Iniciar sesión",
                    style = typo.titleMedium
                )
            }
        }
    }
}
