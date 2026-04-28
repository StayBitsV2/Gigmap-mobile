package com.example.gigmap_frontend_sprint1.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntOffset
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

data class BottomBarItem(val icon: ImageVector)



@Composable
fun BottomBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    val mainColor = Color(0xFF5C0F1A)
    val circleColor = Color(0xFF5C0F1A)
    val selectedIconColor = Color.White
    val unselectedIconColor = Color.White

    val items = listOf(
        BottomBarItem(Icons.Default.Home),
        BottomBarItem(Icons.Default.LocationOn),
        BottomBarItem(Icons.Default.Group),
        BottomBarItem(Icons.Default.Settings)
    )

    val circleRadius = 32.dp
    val iconSize = 28.dp
    val barHeight = 64.dp

    var barSize by remember { mutableStateOf(IntSize(0, 0)) }

    val offsetStep = remember(barSize) {
        barSize.width.toFloat() / (items.size * 2)
    }

    val offset = remember(selectedItem, offsetStep) {
        offsetStep + selectedItem * 2 * offsetStep
    }

    val circleRadiusPx = LocalDensity.current.run { circleRadius.toPx().toInt() }
    val offsetTransition = updateTransition(offset, "offset transition")
    val animation = spring<Float>(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow)

    val cutoutOffset by offsetTransition.animateFloat(
        transitionSpec = {
            if (this.initialState == 0f) {
                snap()
            } else {
                animation
            }
        },
        label = "cutout offset"
    ) { it }

    val circleOffset by offsetTransition.animateIntOffset(
        transitionSpec = {
            if (this.initialState == 0f) {
                snap()
            } else {
                spring(animation.dampingRatio, animation.stiffness)
            }
        },
        label = "circle offset"
    ) {
        IntOffset(it.toInt() - circleRadiusPx, -circleRadiusPx)
    }

    val barShape = remember(cutoutOffset) {
        BottomBarShape(
            offset = cutoutOffset,
            circleRadius = circleRadius,
        )
    }

    Box {
        Box(
            modifier = Modifier
                .offset { circleOffset }
                .zIndex(1f)
                .size(circleRadius * 2)
                .clip(CircleShape)
                .background(circleColor)
                .clickable { onItemSelected(selectedItem) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = items[selectedItem].icon,
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = selectedIconColor
            )
        }
        Row(
            modifier = Modifier
                .onPlaced { barSize = it.size }
                .graphicsLayer {
                    shape = barShape
                    clip = true
                }
                .fillMaxWidth()
                .height(barHeight)
                .background(mainColor),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceAround,
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedItem

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .size(barHeight)
                        .clickable { onItemSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    if (!isSelected) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            modifier = Modifier.size(iconSize),
                            tint = unselectedIconColor
                        )
                    }
                }
            }
        }
    }
}

private class BottomBarShape(
    private val offset: Float,
    private val circleRadius: Dp,
    private val circleGap: Dp = 6.dp,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(getPath(size, density))
    }

    private fun getPath(size: Size, density: Density): Path {
        val cutoutCenterX = offset
        val cutoutRadius = density.run { (circleRadius + circleGap).toPx() }

        return Path().apply {
            val cutoutEdgeOffset = cutoutRadius * 1.5f
            val cutoutLeftX = cutoutCenterX - cutoutEdgeOffset
            val cutoutRightX = cutoutCenterX + cutoutEdgeOffset

            moveTo(x = 0f, y = size.height)

            lineTo(x = 0f, y = 0f)

            lineTo(cutoutLeftX, 0f)

            cubicTo(
                x1 = cutoutCenterX - cutoutRadius,
                y1 = 0f,
                x2 = cutoutCenterX - cutoutRadius,
                y2 = cutoutRadius,
                x3 = cutoutCenterX,
                y3 = cutoutRadius,
            )
            cubicTo(
                x1 = cutoutCenterX + cutoutRadius,
                y1 = cutoutRadius,
                x2 = cutoutCenterX + cutoutRadius,
                y2 = 0f,
                x3 = cutoutRightX,
                y3 = 0f,
            )

            lineTo(x = size.width, y = 0f)

            lineTo(x = size.width, y = size.height)

            lineTo(x = 0f, y = size.height)

            close()
        }
    }
}