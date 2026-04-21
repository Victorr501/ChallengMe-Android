package com.example.challengme.ui.res.values

import androidx.compose.ui.graphics.Color

// ── COLORES PRINCIPALES ──────────────────────────────────────
val Primary       = Color(0xFF3B82F6)   // Botones principales, enlaces, iconos activos
val PrimaryDark   = Color(0xFF1D4ED8)   // Hover de botones, cabeceras
val PrimaryLight  = Color(0xFF93C5FD)   // Textos de énfasis sobre fondo oscuro
val CyanAccent    = Color(0xFF06B6D4)   // Badges, etiquetas de categoría

// ── FONDOS (DARK MODE) ───────────────────────────────────────
val BgDark        = Color(0xFF0F172A)   // Fondo principal de toda la app
val BgCard        = Color(0xFF1E293B)   // Tarjetas, paneles, modales
val BgElevated    = Color(0xFF334155)   // Inputs, dropdowns, hover de items
val Border        = Color(0xFF334155)   // Bordes de tarjetas y separadores

// ── TEXTOS ───────────────────────────────────────────────────
val TextPrimary   = Color(0xFFF8FAFC)   // Texto principal, títulos
val TextSecondary = Color(0xFF94A3B8)   // Texto secundario, subtítulos, placeholders

// ── ESTADOS ──────────────────────────────────────────────────
val Success       = Color(0xFF22C55E)   // Reto completado, confirmaciones
val Warning       = Color(0xFFF59E0B)   // Menos de 2h para expirar, advertencias
val Danger        = Color(0xFFEF4444)   // Errores, racha perdida

// ── RANKING TOP 3 ────────────────────────────────────────────
val RankingOro    = Color(0xFFF59E0B)   // 1ª posición
val RankingPlata  = Color(0xFF94A3B8)   // 2ª posición
val RankingBronce = Color(0xFFCD7C2F)   // 3ª posición

// ── COLORES CON TRANSPARENCIA (para badges y fondos sutiles) ─
// Se usan con el parámetro alpha en Compose directamente:
// Primary.copy(alpha = 0.15f)  →  equivale a rgba(59,130,246,0.15)
// CyanAccent.copy(alpha = 0.15f)
// Success.copy(alpha = 0.15f)
// Warning.copy(alpha = 0.10f)
// Danger.copy(alpha = 0.10f)