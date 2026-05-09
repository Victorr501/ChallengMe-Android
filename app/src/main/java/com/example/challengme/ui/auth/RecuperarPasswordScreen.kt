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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.challengme.data.services.AuthService
import com.example.challengme.ui.res.values.LocalShapes
import com.example.challengme.ui.res.values.LocalSpacing
import kotlinx.coroutines.launch

sealed class RecuperarPasswordUiState {
    object Inicial  : RecuperarPasswordUiState()
    object Cargando : RecuperarPasswordUiState()
    object Enviado  : RecuperarPasswordUiState()
    data class Error(val mensaje: String) : RecuperarPasswordUiState()
}

@Composable
fun RecuperarPasswordScreen(navController: NavController) {
    val spacing = LocalSpacing.current
    val shapes  = LocalShapes.current
    val colors  = MaterialTheme.colorScheme
    val typo    = MaterialTheme.typography
    val scope   = rememberCoroutineScope()

    var email   by remember { mutableStateOf("") }
    var uiState by remember { mutableStateOf<RecuperarPasswordUiState>(RecuperarPasswordUiState.Inicial) }

    val isLoading = uiState is RecuperarPasswordUiState.Cargando
    val isEnviado = uiState is RecuperarPasswordUiState.Enviado

    // TODO: añadir validación de formato de email antes de habilitar el botón
    val isFormValid = email.isNotBlank()

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
            text  = "Restablecer contraseña",
            style = typo.headlineLarge,
            color = colors.onBackground
        )

        Spacer(modifier = Modifier.height(spacing.sm))

        Text(
            text  = "Introduce tu correo y te enviaremos un enlace",
            style = typo.bodyMedium,
            color = colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(spacing.xxl))

        OutlinedTextField(
            value           = email,
            onValueChange   = {
                email = it
                if (uiState is RecuperarPasswordUiState.Error) uiState = RecuperarPasswordUiState.Inicial
            },
            label           = { Text("Correo electrónico") },
            placeholder     = { Text("tucorreo@ejemplo.com") },
            singleLine      = true,
            keyboardOptions = KeyboardOptions(
                keyboardType   = KeyboardType.Email,
                capitalization = KeyboardCapitalization.None
            ),
            modifier        = Modifier.fillMaxWidth(),
            shape           = fieldShape,
            colors          = fieldColors
        )

        Spacer(modifier = Modifier.height(spacing.xl))

        // ── Mensaje de error ──────────────────────────────────
        if (uiState is RecuperarPasswordUiState.Error) {
            val errorMsg = (uiState as RecuperarPasswordUiState.Error).mensaje
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
                    text  = errorMsg,
                    style = typo.bodySmall,
                    color = colors.error
                )
            }
        }

        // ── Estado de éxito ───────────────────────────────────
        if (isEnviado) {
            Text(
                text  = "Si el correo existe, recibirás un enlace en breve",
                style = typo.bodyMedium,
                color = Color(0xFF22C55E)
            )
            Spacer(modifier = Modifier.height(spacing.xl))
        } else {
            Button(
                onClick = {
                    scope.launch {
                        uiState = RecuperarPasswordUiState.Cargando
                        try {
                            AuthService.shared.recuperarPassword(email.trim())
                            uiState = RecuperarPasswordUiState.Enviado
                        } catch (e: Exception) {
                            uiState = RecuperarPasswordUiState.Error(
                                e.message ?: "Error inesperado. Inténtalo de nuevo."
                            )
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
                        text  = "Enviar",
                        style = typo.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.lg))
        }

        Text(
            text     = "← Volver al inicio de sesión",
            style    = typo.bodySmall,
            color    = colors.primary,
            modifier = Modifier.clickable { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.height(spacing.xxl))
    }
}
