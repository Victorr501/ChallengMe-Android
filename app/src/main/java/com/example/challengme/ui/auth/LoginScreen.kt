package com.example.challengme.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.challengme.data.remote.repository.AuthRepository
import com.example.challengme.res.navigation.AuthRoutes
import com.example.challengme.ui.res.values.LocalShapes
import com.example.challengme.ui.res.values.LocalSpacing
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val spacing = LocalSpacing.current
    val shapes  = LocalShapes.current
    val colors  = MaterialTheme.colorScheme
    val typo    = MaterialTheme.typography
    val scope   = rememberCoroutineScope()

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading       by remember { mutableStateOf(false) }
    var errorMessage    by remember { mutableStateOf<String?>(null) }

    // Formulario válido: email no vacío y contraseña ≥ 6 caracteres
    val isFormValid = email.isNotBlank() && password.length >= 6

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor      = colors.surfaceVariant,
        unfocusedContainerColor    = colors.surfaceVariant,
        focusedBorderColor         = colors.primary,
        unfocusedBorderColor       = colors.outline,
        focusedTextColor           = colors.onBackground,
        unfocusedTextColor         = colors.onBackground,
        focusedPlaceholderColor    = colors.onSurfaceVariant,
        unfocusedPlaceholderColor  = colors.onSurfaceVariant,
        cursorColor                = colors.primary,
        focusedLabelColor          = colors.primary,
        unfocusedLabelColor        = colors.onSurfaceVariant,
        focusedTrailingIconColor   = colors.onSurfaceVariant,
        unfocusedTrailingIconColor = colors.onSurfaceVariant,
    )
    val fieldShape = RoundedCornerShape(shapes.md)

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(spacing.xxl))

        Text(
            text  = "Iniciar sesión",
            style = typo.headlineLarge,
            color = colors.onBackground
        )

        Spacer(modifier = Modifier.height(spacing.xxl))

        OutlinedTextField(
            value           = email,
            onValueChange   = { email = it; errorMessage = null },
            label           = { Text("Correo electrónico") },
            placeholder     = { Text("tucorreo@ejemplo.com") },
            singleLine      = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier        = Modifier.fillMaxWidth(),
            shape           = fieldShape,
            colors          = fieldColors
        )

        Spacer(modifier = Modifier.height(spacing.md))

        OutlinedTextField(
            value                = password,
            onValueChange        = { password = it; errorMessage = null },
            label                = { Text("Contraseña") },
            singleLine           = true,
            keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            trailingIcon         = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector        = if (passwordVisible) Icons.Filled.VisibilityOff
                                             else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Ocultar contraseña"
                                             else "Mostrar contraseña"
                    )
                }
            },
            modifier             = Modifier.fillMaxWidth(),
            shape                = fieldShape,
            colors               = fieldColors
        )

        Spacer(modifier = Modifier.height(spacing.sm))

        Text(
            text     = "¿Olvidaste tu contraseña?",
            style    = typo.bodySmall,
            color    = colors.primary,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { }
        )

        Spacer(modifier = Modifier.height(spacing.xl))

        // ── Mensaje de error ──────────────────────────────────
        errorMessage?.let { msg ->
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.md)
            ) {
                Icon(
                    imageVector        = Icons.Filled.Error,
                    contentDescription = null,
                    tint               = colors.error,
                    modifier           = Modifier.size(16.dp)
                )
                Text(
                    text  = msg,
                    style = typo.bodySmall,
                    color = colors.error
                )
            }
        }

        // ── Botón principal ───────────────────────────────────
        Button(
            onClick = {
                scope.launch {
                    isLoading    = true
                    errorMessage = null
                    try {
                        AuthRepository.shared.loginEmail(email.trim(), password)
                        // Éxito → AuthManager.isAuthenticated = true
                        // MainNavGraph navega automáticamente a MainLayout
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Error inesperado. Inténtalo de nuevo."
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled  = isFormValid && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape    = RoundedCornerShape(shapes.md),
            colors   = ButtonDefaults.buttonColors(
                containerColor         = colors.primary,
                disabledContainerColor = colors.surfaceVariant,
                contentColor           = colors.onPrimary,
                disabledContentColor   = colors.onSurfaceVariant
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(20.dp),
                    color       = colors.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text  = "Iniciar sesión",
                    style = typo.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text  = "¿No tienes cuenta? ",
                style = typo.bodySmall,
                color = colors.onSurfaceVariant
            )
            Text(
                text     = "Regístrate",
                style    = typo.bodySmall,
                color    = colors.primary,
                modifier = Modifier.clickable {
                    navController.navigate(AuthRoutes.REGISTER) {
                        popUpTo(AuthRoutes.WELCOME)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(spacing.xxl))
    }
}
