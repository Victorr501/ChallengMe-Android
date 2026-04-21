package com.example.challengme.ui.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.challengme.ui.res.values.LocalSpacing

@Composable
fun HistorialScreen() {
    val spacing   = LocalSpacing.current
    val colors    = MaterialTheme.colorScheme
    val typo      = MaterialTheme.typography
    val primary   = colors.primary
    val secondary = colors.secondary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colors.background, colors.surface)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            Icon(
                imageVector        = Icons.Filled.List,
                contentDescription = null,
                tint               = colors.primary,
                modifier           = Modifier
                    .size(52.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        val brush = Brush.linearGradient(
                            colors = listOf(primary, secondary),
                            start  = Offset.Zero,
                            end    = Offset(size.width, size.height)
                        )
                        onDrawWithContent {
                            drawContent()
                            drawRect(brush = brush, blendMode = BlendMode.SrcAtop)
                        }
                    }
            )

            Text(
                text  = "Mis Retos",
                style = typo.headlineMedium,
                color = colors.onBackground
            )

            Text(
                text  = "Próximamente…",
                style = typo.bodySmall,
                color = colors.onSurfaceVariant
            )
        }
    }
}
