package io.getstream.webrtc.sample.compose.car.StateBar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.webrtc.sample.compose.R
import kotlin.math.roundToInt


@Composable
fun CarStatusBar(
  viewModel: MyStateBarViewModel
){
  val uiState by viewModel.uiState.collectAsState()
  Row{
    CarBatteryVolume(viewModel = viewModel)
    CarSignalStrength(stren = uiState.signalStrength)
  }
}
private fun Density.createStripeBrush(
  stripeColor: Color,
  stripeWidth: Dp,
  stripeToGapRatio: Float
): Brush {
  val stripeWidthPx = stripeWidth.toPx()
  val stripeGapWidthPx = stripeWidthPx / stripeToGapRatio
  val brushSizePx = stripeGapWidthPx + stripeWidthPx
  val stripeStart = stripeGapWidthPx / brushSizePx

  return Brush.linearGradient(
    stripeStart to Color.Transparent,
    stripeStart to stripeColor,
    start = Offset(0f, 0f),
    end = Offset(brushSizePx, brushSizePx),
    tileMode = TileMode.Repeated
  )
}

@Composable
fun CarBatteryVolume(
  viewModel: MyStateBarViewModel
){
    val uiState by viewModel.uiState.collectAsState()
    val stripeWidth=2.dp
    val strokeWidth=10f


    Box(
    modifier= Modifier
      .size(40.dp, 40.dp)
      .border(width = 1.dp, color = Color.Black)
      .drawWithCache {

        val brush = createStripeBrush(
          stripeColor = Color.Blue,
          stripeWidth = stripeWidth,
          stripeToGapRatio = 1.8f
        )
        onDrawBehind {
          drawArc(
            brush = brush,
            startAngle = 0f,
            sweepAngle = -90f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
          )
          drawArc(
            color = Color.Red,
            startAngle = -90f,
            sweepAngle = -270f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
          )
        }
      },
      contentAlignment= Alignment.Center
    ){
      Text(text=uiState.batteryVolume.roundToInt().toString(),
        fontSize = 24.sp,
        color = Color.Black,
      )
    }




}


@Composable
fun CarSignalStrength(
  stren:Float
){
  Box(
    contentAlignment = Alignment.CenterEnd
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        painter = painterResource(R.drawable.baseline_directions_car_24),
        "baseline car"
      )
      Text(
        text="$stren%",
        fontSize = 24.sp
      )
    }
  }
}