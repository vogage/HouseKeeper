package io.getstream.webrtc.sample.compose.car.JoyStick

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.getstream.webrtc.sample.compose.ui.theme.LightBlue
import io.getstream.webrtc.sample.compose.ui.theme.MidBlue
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
      .size(buttonSize * 2)
      .background(Color.White, CircleShape)
      .border(2.dp, Color.Black, CircleShape)
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
        }
        .width(buttonSize)
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
              viewModel.refreshOffset(offsetX.value, offsetY.value)
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
              val newOffsetX = offsetX.value + dragAmount.x * directionFactor
              val newOffsetY = offsetY.value + dragAmount.y
              viewModel.refreshOffset(newOffsetX, newOffsetY)
              scope.launch {

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



@Composable
fun MyJoyStick2(
  viewModel:MyJoyStickViewModel
){

  Box(
    modifier = Modifier
   //   .border(width = 1.dp, color = Color.Black)
  ) {


    val layoutDirection = LocalLayoutDirection.current
    val directionFactor = if (layoutDirection == LayoutDirection.Rtl) -1 else 1
    val scope = rememberCoroutineScope()


    val buttonSize = 55.dp
    val PannelSize= 145.dp


    val triangle_width= 37.24.dp
    val triangle_height= 22.5.dp


    // Swipe size in px
    val buttonSizePx =  with(LocalDensity.current){buttonSize.toPx() }
    val PannelSizePx= with(LocalDensity.current){PannelSize.toPx()}
    val triangle_widthPx=  with(LocalDensity.current){triangle_width.toPx()}
    val triangle_heightPx= with(LocalDensity.current){triangle_height.toPx()}



    // Drag offset

    val controlButtonCenterXPx = remember { Animatable(0f) }
    val controlButtonCenterYPx = remember { Animatable(0f) }
    val controlRadius=(PannelSizePx-buttonSizePx)/2

    fun rePositionOffset(dx:Float,dy:Float):Offset{
      val newOffset= Offset(controlButtonCenterXPx.value+dx,controlButtonCenterYPx.value+dy)
      val r= sqrt(newOffset.x*newOffset.x+newOffset.y*newOffset.y)
      return if(r>controlRadius){
        Offset(controlRadius*newOffset.x/r,controlRadius*newOffset.y/r)
      }else{
        newOffset
      }
    }

    Box(
      modifier = Modifier
       // .alpha(0.4f)
        .padding(1.dp)
        .size(width =PannelSize, height = PannelSize )
   //     .border(width = 1.dp, color = Color.Green)
        .background(color = Color.LightGray, shape = CircleShape),
      contentAlignment = Alignment.Center

    ){
      Canvas(
        modifier= Modifier
          .padding(16.dp)


      ){
        val triangleCenter1= Offset((PannelSizePx-buttonSizePx)/4+buttonSizePx/2,0f)
        val trianglePath1= Path().apply{
          moveTo(triangleCenter1.x+triangle_heightPx/2,triangleCenter1.y)
          lineTo(triangleCenter1.x-triangle_heightPx/2,triangle_widthPx/2)
          lineTo(triangleCenter1.x-triangle_heightPx/2,-triangle_widthPx/2)
          lineTo(triangleCenter1.x+triangle_heightPx/2,triangleCenter1.y)
        }

        val triangleCenter2= Offset(0f,(PannelSizePx-buttonSizePx)/4+buttonSizePx/2)
        val trianglePath2= Path().apply{
          moveTo(triangleCenter2.x,triangleCenter2.y+triangle_heightPx/2)
          lineTo(triangle_widthPx/2,triangleCenter2.y-triangle_heightPx/2)
          lineTo(-triangle_widthPx/2,triangleCenter2.y-triangle_heightPx/2)
          lineTo(triangleCenter2.x,triangleCenter2.y+triangle_heightPx/2)
        }

        val triangleCenter3= Offset(-(PannelSizePx-buttonSizePx)/4-buttonSizePx/2,0f)
        val trianglePath3= Path().apply{
          moveTo(triangleCenter3.x-triangle_heightPx/2,triangleCenter3.y)
          lineTo(triangleCenter3.x+triangle_heightPx/2,triangleCenter3.y+triangle_widthPx/2)
          lineTo(triangleCenter3.x+triangle_heightPx/2,triangleCenter3.y-triangle_widthPx/2)
          lineTo(triangleCenter3.x-triangle_heightPx/2,triangleCenter3.y)
        }

        val triangleCenter4= Offset(0f,-(PannelSizePx-buttonSizePx)/4-buttonSizePx/2)
        val trianglePath4= Path().apply{
          moveTo(triangleCenter4.x,triangleCenter4.y-triangle_heightPx/2)
          lineTo(triangle_widthPx/2,triangleCenter4.y+triangle_heightPx/2)
          lineTo(-triangle_widthPx/2,triangleCenter4.y+triangle_heightPx/2)
          lineTo(triangleCenter4.x,triangleCenter4.y-triangle_heightPx/2)
        }

        drawPath(trianglePath1, color =LightBlue, alpha = 0.6f)
        drawPath(trianglePath2, color =LightBlue, alpha = 0.6f)
        drawPath(trianglePath3, color =LightBlue, alpha = 0.6f)
        drawPath(trianglePath4, color =LightBlue, alpha = 0.6f)

      }
      Box(
        modifier = Modifier
          .size(width=buttonSize,height=buttonSize)
          .offset { IntOffset(controlButtonCenterXPx.value.roundToInt(),controlButtonCenterYPx.value.roundToInt())}
//          .border(width = 1.dp, color = Color.Black)
          .background(color = MidBlue, shape = CircleShape)
          .pointerInput(Unit) {
            detectDragGestures(
              onDrag = { change, dragAmount ->
                change.consume()
                viewModel.refreshIsDragging(true)
                val newOffset = rePositionOffset(dragAmount.x,dragAmount.y)
                viewModel.refreshOffset(newOffset.x,newOffset.y)

                scope.launch {
                  controlButtonCenterXPx.snapTo(newOffset.x)
                }
                scope.launch {
                  controlButtonCenterYPx.snapTo(newOffset.y)
                }
              },
              onDragStart = {
                viewModel.refreshIsDragging(true)
              },
              onDragEnd = {
                  scope.launch {
                    controlButtonCenterXPx.animateTo(0f)

                  }
                  scope.launch {
                    controlButtonCenterYPx.animateTo(0f)
                  }
              },
              onDragCancel = {}
            )
          },

      )
    }


  }
}



