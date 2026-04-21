package com.example.challengme.ui.res.values

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.challengme.R

// ── PROVEEDOR DE GOOGLE FONTS ────────────────────────────────
//
// Esto le dice a Compose que las fuentes vienen de Google Fonts.
// Necesita que añadas en res/values/font_certs.xml el certificado
// de Google (se explica abajo).

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

// ── FAMILIAS TIPOGRÁFICAS ────────────────────────────────────
//
// Inter  → fuente del cuerpo (--font-body en el CSS)
// Syne   → fuente de display y títulos grandes (--font-display en el CSS)

private val InterFont = GoogleFont("Inter")
private val SyneFont  = GoogleFont("Syne")

val InterFamily = FontFamily(
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.ExtraBold),
)

val SyneFamily = FontFamily(
    Font(googleFont = SyneFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = SyneFont, fontProvider = provider, weight = FontWeight.ExtraBold),
)

// ── ESCALA TIPOGRÁFICA ───────────────────────────────────────
//
//  displayLarge   → .text-display : Syne 800, 40sp  → puntos totales, racha
//  headlineLarge  → h1            : Syne 700, 28sp  → título del reto del día
//  headlineMedium → h2            : Inter 600, 20sp → títulos de sección
//  titleMedium    → h3            : Inter 600, 16sp → subtítulos de tarjeta
//  bodyLarge      → p / .body     : Inter 400, 16sp → descripción del reto
//  bodySmall      → .text-sm      : Inter 400, 14sp → metadatos, fechas
//  labelSmall     → .text-xs      : Inter 500, 12sp → badges, labels de input

val ChallengMeTypography = Typography(

    // Display — puntos totales, número de racha principal
    displayLarge = TextStyle(
        fontFamily    = SyneFamily,
        fontWeight    = FontWeight.ExtraBold,
        fontSize      = 40.sp,
        lineHeight    = 44.sp,
        letterSpacing = (-0.8).sp
    ),

    // Heading 1 — título del reto del día
    headlineLarge = TextStyle(
        fontFamily    = SyneFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 28.sp,
        lineHeight    = 34.sp,
        letterSpacing = (-0.28).sp
    ),

    // Heading 2 — títulos de sección
    headlineMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 20.sp,
        lineHeight = 26.sp
    ),

    // Heading 3 — subtítulos dentro de tarjetas
    titleMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 16.sp,
        lineHeight = 22.sp
    ),

    // Body — descripción del reto, textos generales
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 25.6.sp
    ),

    // Small — metadatos, fechas, etiquetas
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp
    ),

    // Micro — badges, contadores, labels de input
    labelSmall = TextStyle(
        fontFamily    = InterFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.6.sp
    )
)