package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.random.Random

data class HeartParticle(
    val initialXRatio: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float,
    val color: Color
)

@Composable
fun FloatingHeartsCanvas(
    modifier: Modifier = Modifier,
    particleCount: Int = 18
) {
    val particles = remember {
        val colors = listOf(
            Color(0xFFE11D48),
            Color(0xFFFB7185),
            Color(0xFFFDA4AF),
            Color(0xFFF59E0B)
        )
        List(particleCount) {
            HeartParticle(
                initialXRatio = Random.nextFloat(),
                speed = 0.08f + Random.nextFloat() * 0.12f,
                size = 14f + Random.nextFloat() * 22f,
                alpha = 0.25f + Random.nextFloat() * 0.4f,
                color = colors[Random.nextInt(colors.size)]
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "floating_hearts")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "heart_time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEachIndexed { index, p ->
            val progress = (time * p.speed * 10f + (index * 0.15f)) % 1f
            val y = canvasHeight * (1f - progress)
            val wobble = kotlin.math.sin((progress * 4.0 * kotlin.math.PI) + index).toFloat() * 24f
            val x = (p.initialXRatio * canvasWidth + wobble).coerceIn(20f, canvasWidth - 20f)

            drawHeart(
                center = Offset(x, y),
                size = p.size,
                color = p.color.copy(alpha = p.alpha * (1f - (progress * 0.6f)))
            )
        }
    }
}

private fun DrawScope.drawHeart(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        val w = size
        val h = size
        val top = center.y - h / 2f
        val left = center.x - w / 2f

        moveTo(left + w / 2f, top + h / 4f)
        cubicTo(
            left + w / 2f, top,
            left, top,
            left, top + h / 3f
        )
        cubicTo(
            left, top + h * 0.65f,
            left + w / 2f, top + h * 0.85f,
            left + w / 2f, top + h
        )
        cubicTo(
            left + w / 2f, top + h * 0.85f,
            left + w, top + h * 0.65f,
            left + w, top + h / 3f
        )
        cubicTo(
            left + w, top,
            left + w / 2f, top,
            left + w / 2f, top + h / 4f
        )
        close()
    }
    drawPath(path = path, color = color)
}
