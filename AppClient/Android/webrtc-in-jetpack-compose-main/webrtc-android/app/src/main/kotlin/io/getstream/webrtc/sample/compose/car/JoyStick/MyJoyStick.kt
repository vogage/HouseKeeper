package io.getstream.webrtc.sample.compose.car.JoyStick

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun MyJoyStick(
  viewModel: MyJoyStickViewModel
) {
  // Support RTL
  val layoutDirection = LocalLayoutDirection.current
  val directionFactor = if (layoutDirection == LayoutDirection.Rtl) -1 else 1
  val scope = rememberCoroutineScope()


  val buttonSize = 90.dp


  // Swipe size in px
  val buttonSizePx = with(LocalDensity.current) { buttonSize.toPx() }
 // val dragSizePx = buttonSizePx * 1.5f
  // Drag offset
  val offsetX = remember { Animatable(0f) }
  val offsetY = remember { Animatable(0f) }


  LaunchedEffect(offsetX.value, offsetY.value) {
    viewModel.refreshPosition(offsetX.value, offsetY.value)
  }

  Box(
    modifier = Modifier
      .size(buttonSize*2)
      .background(Color.White, CircleShape)
      .border(2.dp, Color.Black,CircleShape)
    ,
    contentAlignment = Alignment.Center
  ) {

    Box(
      modifier = Modifier
        .offset {
          IntOffset(
            x = (offsetX.value).roundToInt(),
            y = (offsetY.value).roundToInt()
          )
        }.width(buttonSize)
        .height(buttonSize)
        .background(Color.DarkGray, CircleShape)
        .alpha(0.8f)
        .pointerInput(Unit) {
          detectDragGestures(
            onDragStart = {
              viewModel.refreshIsDragging(true)
            },
            onDragEnd = {
              scope.launch {
                offsetX.animateTo(0f)
              }
              scope.launch {
                offsetY.animateTo(0f)
              }
              viewModel.refreshIsDragging(false)
            },
            onDragCancel = {
              scope.launch {
                offsetX.animateTo(0f)
              }
              scope.launch {
                offsetY.animateTo(0f)
              }
              viewModel.refreshIsDragging(false)
            },
            onDrag = { change, dragAmount ->
              change.consume()

              scope.launch {
                val newOffsetX = offsetX.value + dragAmount.x * directionFactor
                val newOffsetY = offsetY.value + dragAmount.y
                viewModel.refreshOffset(newOffsetX,newOffsetY)
                if (
                  sqrt(newOffsetX.pow(2) + newOffsetY.pow(2)) < buttonSizePx
                ) {
                  offsetX.snapTo(newOffsetX)
                  offsetY.snapTo(newOffsetY)
                } else if (
                  sqrt(offsetX.value.pow(2) + newOffsetY.pow(2)) < buttonSizePx
                ) {
                  offsetY.snapTo(newOffsetY)
                } else if (
                  sqrt(newOffsetX.pow(2) + offsetY.value.pow(2)) < buttonSizePx
                ) {
                  offsetX.snapTo(newOffsetX)
                }
              }

            }
          )
        },

      ) {

    }
  }

  Box(modifier = Modifier.size(40.dp)){

  }
}




