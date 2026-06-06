package com.diazmoviles.app.presentation.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp)
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(animation = tween(1200)),
        label = "shimmerX"
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.3f),
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.3f),
        ),
        start = Offset(translateX, 0f),
        end = Offset(translateX + 200f, 0f)
    )
    Box(
        modifier = modifier.clip(shape).background(brush)
    )
}

@Composable
fun ProductShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(12.dp)) {
        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(140.dp),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
        )
        Spacer(Modifier.height(8.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.8f).height(16.dp))
        Spacer(Modifier.height(4.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.5f).height(12.dp))
        Spacer(Modifier.height(8.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth(0.4f).height(20.dp))
    }
}
