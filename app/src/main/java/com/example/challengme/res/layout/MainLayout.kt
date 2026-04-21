package com.example.challengme.res.layout

// ============================================================
//  MainLayout.kt
//  ChallengMe
//
//  Shell principal de la app tras el login.
//  Contiene el top bar, el bottom tab bar y el sheet de perfil.
//  El contenido de cada tab vive en su propia pantalla.
// ============================================================

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.challengme.data.local.AuthManager
import com.example.challengme.data.local.JwtClaims
import com.example.challengme.ui.dashboard.DashboardScreen
import com.example.challengme.ui.historial.HistorialScreen
import com.example.challengme.ui.ranking.RankingScreen
import com.example.challengme.ui.reto.RetoScreen
import com.example.challengme.ui.res.values.LocalShapes
import com.example.challengme.ui.res.values.LocalSpacing

// ── Definición de tabs ────────────────────────────────────────

enum class AppTab(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Filled.Home),
    RETO("Reto",           Icons.Filled.Bolt),
    HISTORIAL("Mis Retos", Icons.Filled.List),
    RANKING("Ranking",     Icons.Filled.EmojiEvents)
}

// ── Shell principal ───────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout() {
    val auth    = AuthManager.shared
    val claims  by auth.claims.collectAsState()
    val colors  = MaterialTheme.colorScheme
    val spacing = LocalSpacing.current
    val shapes  = LocalShapes.current
    val typo    = MaterialTheme.typography

    var selectedTab      by remember { mutableStateOf(AppTab.DASHBOARD) }
    var showProfileSheet by remember { mutableStateOf(false) }

    val gradientBrush = Brush.linearGradient(listOf(colors.primary, colors.secondary))
    val initial       = claims?.nombreUsuario?.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // ── Top Bar ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = spacing.lg)
                .padding(top = spacing.md, bottom = spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = selectedTab.label,
                    style = typo.headlineLarge,
                    color = colors.onBackground
                )
                val nombre = claims?.nombreUsuario
                if (nombre != null) {
                    Text(
                        text  = "Hola, $nombre 👋",
                        style = typo.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            // Avatar circular con gradiente azul → cyan
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(brush = gradientBrush, shape = CircleShape)
                    .clickable { showProfileSheet = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = initial,
                    style = typo.titleMedium,
                    color = colors.onPrimary
                )
            }
        }

        HorizontalDivider(color = colors.outline)

        // ── Contenido del tab activo ──────────────────────────
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                AppTab.DASHBOARD -> DashboardScreen()
                AppTab.RETO      -> RetoScreen()
                AppTab.HISTORIAL -> HistorialScreen()
                AppTab.RANKING   -> RankingScreen()
            }
        }

        // ── Bottom Bar ────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp)
                .background(colors.surface)
                .padding(top = spacing.sm)
                .navigationBarsPadding()
                .padding(bottom = spacing.lg)
        ) {
            AppTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                val scale by animateFloatAsState(
                    targetValue    = if (isSelected) 1.1f else 1.0f,
                    animationSpec  = spring(),
                    label          = "scale_${tab.name}"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = tab }
                        .padding(vertical = spacing.xs),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.xs)
                ) {
                    Icon(
                        imageVector        = tab.icon,
                        contentDescription = tab.label,
                        tint               = if (isSelected) colors.primary else colors.onSurfaceVariant,
                        modifier           = Modifier
                            .size(24.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                    )
                    Text(
                        text  = tab.label,
                        style = typo.labelSmall,
                        color = if (isSelected) colors.primary else colors.onSurfaceVariant
                    )
                }
            }
        }
    }

    // ── BottomSheet de perfil ─────────────────────────────────
    if (showProfileSheet) {
        ProfileBottomSheet(
            claims    = claims,
            onDismiss = { showProfileSheet = false },
            onLogout  = {
                AuthManager.shared.logout()
                showProfileSheet = false
            }
        )
    }
}

// ── BottomSheet de perfil ─────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileBottomSheet(
    claims:    JwtClaims?,
    onDismiss: () -> Unit,
    onLogout:  () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors  = MaterialTheme.colorScheme
    val spacing = LocalSpacing.current
    val shapes  = LocalShapes.current
    val typo    = MaterialTheme.typography

    val gradientBrush = Brush.linearGradient(listOf(colors.primary, colors.secondary))
    val initial       = claims?.nombreUsuario?.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = colors.surface,
        dragHandle       = {
            // Indicador de arrastre manual para usar el color del tema
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.md),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 4.dp)
                        .background(colors.outline, CircleShape)
                )
            }
        }
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Avatar grande ─────────────────────────────────
            Box(
                modifier         = Modifier
                    .size(72.dp)
                    .background(brush = gradientBrush, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = initial,
                    style = typo.headlineLarge,
                    color = colors.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            claims?.nombreUsuario?.let {
                Text(text = it, style = typo.headlineMedium, color = colors.onBackground)
            }
            claims?.correo?.let {
                Text(text = it, style = typo.bodySmall, color = colors.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(spacing.xxl))

            HorizontalDivider(color = colors.outline)

            // ── Opciones ──────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.lg)
                    .padding(top = spacing.lg)
                    .background(colors.surfaceVariant, RoundedCornerShape(shapes.lg))
                    .border(1.dp, colors.outline, RoundedCornerShape(shapes.lg))
            ) {
                ProfileOption(
                    icon    = Icons.Filled.Person,
                    label   = "Perfil",
                    tint    = colors.onBackground,
                    onClick = onDismiss // TODO: navegar a pantalla de perfil
                )

                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color    = colors.outline
                )

                ProfileOption(
                    icon    = Icons.Filled.ExitToApp,
                    label   = "Cerrar sesión",
                    tint    = colors.error,
                    onClick = onLogout
                )
            }
        }
    }
}

// ── Fila de opción dentro del sheet ──────────────────────────

@Composable
private fun ProfileOption(
    icon:    ImageVector,
    label:   String,
    tint:    Color,
    onClick: () -> Unit
) {
    val colors  = MaterialTheme.colorScheme
    val spacing = LocalSpacing.current
    val typo    = MaterialTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = spacing.lg, vertical = spacing.md),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = tint,
            modifier           = Modifier.size(24.dp)
        )
        Text(
            text     = label,
            style    = typo.bodyLarge,
            color    = tint,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector        = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint               = colors.onSurfaceVariant,
            modifier           = Modifier.size(16.dp)
        )
    }
}
