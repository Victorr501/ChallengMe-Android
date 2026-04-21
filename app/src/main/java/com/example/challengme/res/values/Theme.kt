package com.example.challengme.ui.res.values

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── ESQUEMA DE COLOR ─────────────────────────────────────────
//
// Material3 usa un sistema de "roles" de color (primary, surface, etc.)
// Aquí mapeamos los colores del CSS a esos roles:
//
//  primary        → Primary (#3B82F6)   botones principales
//  onPrimary      → Blanco              texto sobre botón primario
//  secondary      → CyanAccent          badges, etiquetas
//  background     → BgDark              fondo de pantalla
//  surface        → BgCard              tarjetas, paneles
//  surfaceVariant → BgElevated          inputs, dropdowns
//  onBackground   → TextPrimary         texto sobre fondo
//  onSurface      → TextPrimary         texto sobre tarjeta
//  onSurfaceVariant → TextSecondary     texto secundario
//  error          → Danger              errores

private val ChallengMeColorScheme = darkColorScheme(
    primary             = Primary,
    onPrimary           = TextPrimary,
    primaryContainer    = PrimaryDark,
    onPrimaryContainer  = PrimaryLight,
    secondary           = CyanAccent,
    onSecondary         = BgDark,
    background          = BgDark,
    onBackground        = TextPrimary,
    surface             = BgCard,
    onSurface           = TextPrimary,
    surfaceVariant      = BgElevated,
    onSurfaceVariant    = TextSecondary,
    outline             = Border,
    error               = Danger,
    onError             = TextPrimary,
)

// ── ESPACIADO ────────────────────────────────────────────────
//
// Equivalencias directas con las variables CSS:
//   --space-xs  : 4dp
//   --space-sm  : 8dp
//   --space-md  : 16dp
//   --space-lg  : 24dp
//   --space-xl  : 32dp
//   --space-2xl : 48dp

data class ChallengMeSpacing(
    val xs:  Dp = 4.dp,
    val sm:  Dp = 8.dp,
    val md:  Dp = 16.dp,
    val lg:  Dp = 24.dp,
    val xl:  Dp = 32.dp,
    val xxl: Dp = 48.dp,
)

// ── BORDES ────────────────────────────────────────────────────
//
// Equivalencias con el CSS:
//   --radius-sm   : 6dp
//   --radius-md   : 12dp
//   --radius-lg   : 16dp
//   --radius-xl   : 24dp
//   --radius-full : 9999dp (círculo — usar CircleShape en Compose)

data class ChallengMeShapes(
    val sm:   Dp = 6.dp,
    val md:   Dp = 12.dp,
    val lg:   Dp = 16.dp,
    val xl:   Dp = 24.dp,
    val full: Dp = 9999.dp,
)

// ── PROVISION DE TOKENS CUSTOM ───────────────────────────────
//
// CompositionLocal permite que cualquier Composable hijo acceda
// al espaciado y a los shapes sin necesidad de pasarlos como parámetros.

val LocalSpacing = staticCompositionLocalOf { ChallengMeSpacing() }
val LocalShapes  = staticCompositionLocalOf { ChallengMeShapes() }

// ── TEMA PRINCIPAL ────────────────────────────────────────────
//
// Envuelve TODA tu app en este composable dentro de MainActivity.
//
// Ejemplo de uso en MainActivity.kt:
//
//   setContent {
//       ChallengMeTheme {
//           // tu NavHost o pantalla raíz aquí
//       }
//   }
//
// Ejemplo de uso en cualquier Composable:
//
//   val spacing = LocalSpacing.current
//   val shapes  = LocalShapes.current
//   val colors  = MaterialTheme.colorScheme
//   val typo    = MaterialTheme.typography
//
//   Text(
//       text  = "Reto del día",
//       style = typo.headlineLarge,
//       color = colors.onBackground
//   )
//
//   Box(
//       modifier = Modifier
//           .background(colors.surface)
//           .padding(spacing.lg)
//   )

@Composable
fun ChallengMeTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSpacing provides ChallengMeSpacing(),
        LocalShapes  provides ChallengMeShapes(),
    ) {
        MaterialTheme(
            colorScheme = ChallengMeColorScheme,
            typography  = ChallengMeTypography,
            content     = content
        )
    }
}