package io.getstream.webrtc.sample.compose.car.StateBar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.webrtc.sample.compose.R
import kotlin.math.roundToInt

enum class CarSignalLevel{
  NoSignal,
  VeryLow,
  Low,
  Good,
  VeryGood,
  Perfect
}

@Composable
fun CarStatusBar(
  viewModel: CarStateBarViewModel
){
  val uiState by viewModel.uiState.collectAsState()
  Row(
    horizontalArrangement = Arrangement.End
  ) {
    CarBatteryVolume2(batteryVolume = uiState.batteryVolume)
    Spacer(modifier = Modifier.width(10.dp))
    CarSignalStrength(signalStrength = uiState.signalStrength)
  }
}



@Composable
fun CarBatteryVolume(
  batteryVolume:Float// car batteryVolume
){
    val stripeWidth=2.dp
    val strokeWidth=10f

    val consumedSweepAngle=-(batteryVolume/100f)*360f
    val remainSweepAngle=-360f-consumedSweepAngle

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
            sweepAngle = consumedSweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
          )
          drawArc(
            color = Color.Red,
            startAngle = consumedSweepAngle,
            sweepAngle = remainSweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
          )
        }
      },
      contentAlignment= Alignment.Center
    ){
      Text(text=batteryVolume.roundToInt().toString(),
        fontSize = 24.sp,
        color = Color.Black,
      )
    }




}


@Composable
fun CarBatteryVolume2(
  batteryVolume:Float// car batteryVolume
){
  val stripeWidth=2.dp
  val strokeWidth=10f

  val consumedSweepAngle=(batteryVolume/100f)*360f
  val remainSweepAngle=360f-consumedSweepAngle
  Box(
    modifier= Modifier
      .size(40.dp, 40.dp)
      .border(width = 1.dp, color = Color.Black)
      .drawBehind {
        val brush = createStripeBrush(
          stripeColor = Color.Blue,
          stripeWidth = stripeWidth,
          stripeToGapRatio = 1.2f
        )

        drawArc(
          brush = brush,
          startAngle = -90f,
          sweepAngle = consumedSweepAngle,
          useCenter = false,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round),

          )
        drawArc(
          color = Color.Red,
          startAngle = consumedSweepAngle - 90f,
          sweepAngle = remainSweepAngle,
          useCenter = false,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

      },
    contentAlignment= Alignment.Center
  ){
    Text(text=batteryVolume.roundToInt().toString(),
      fontSize = 24.sp,
      color = Color.Black,
    )
  }
}




@Composable
fun CarSignalStrength(
  signalStrength:Float
){
  Box(
    contentAlignment = Alignment.CenterEnd
  ) {
    Row(
      verticalAlignment = Alignment.Bottom
    ) {
      Icon(
        painter = painterResource(R.drawable.baseline_directions_car_24),
        "baseline car"
      )
      CarSignalIndicator(signalStrength)

      Text(
        text="${signalStrength.roundToInt()}%",
        fontSize = 24.sp,
        color= SignalIndicatorColor(signalStrength/100)
      )
    }
  }
}



@Composable
fun CarSignalIndicator(
  sst: Float //SignalStrength
){
  val ssth=sst/100;
  val sslp:List<Float> =listOf(0.3f,0.45f,0.60f,0.75f,0.9f,1f)//SignalStrengthLevelPercent
  val sslw:List<Float> = listOf(0f,0.2f,0.4f,0.6f,0.8f,1f)
  val sstlvs=SignalStrengthLevelList(ssth,sslw)
  val signalColor= SignalIndicatorColor(ssth)




  val signalHeight=30.dp
  val signalWidth=4.dp
  val signalRectInterval=1.dp

  val signalHeightPx=with(LocalDensity.current){signalHeight.toPx()}
  val signalWidthPx=with(LocalDensity.current){(signalWidth+signalRectInterval).toPx()}
  val signalBodyWidthPx=with(LocalDensity.current){signalWidth.toPx()}

  Box(
    modifier = Modifier
      .size(35.dp, 30.dp)
      .border(width = 1.dp, color = Color.Black),

    ){
    Canvas(
      modifier = Modifier,
      onDraw ={
        for(i in sstlvs.indices){
          val signalWidthScale=sslw[i]
          val signalHeightScale=sslp[i]
          val h=signalHeightPx*signalHeightScale
          val tpy=signalHeightPx-h
          val blx=signalWidthPx*6f*signalWidthScale //rect left bottom point
          val brx=blx+signalWidthPx//rect right bottom point
          drawRect(
            color =signalColor,
            topLeft = Offset(blx,tpy),
            size= Size(signalBodyWidthPx,h),
            alpha = if(sstlvs[i]) 1.0f else 0f
          )
        }
      }
    )
  }
}
@Composable
fun SliderOfBatteryVolume(
  OnchangePosition: (Float) -> Unit
){
  var sliderPosition by remember{ mutableFloatStateOf(0f) }
  Column {
    Slider(
      value=sliderPosition,
      onValueChange = {

        sliderPosition= it
        OnchangePosition(it*100)
      }
    )
    Text(text=(sliderPosition*100).toString())
  }
}



@Composable
fun SliderOfSignalStrength(
  OnchangePosition: (Float) -> Unit
){
  var sliderPosition by remember{ mutableFloatStateOf(0f) }
  Column {
    Slider(
      value=sliderPosition,
      onValueChange = {

        sliderPosition= it
        OnchangePosition(it*100)
      }
    )
    Text(text=(sliderPosition*100).toString())
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

private fun SignalStrengthLevelList(sst: Float,signalStrengthLevelPercent:List<Float>):List<Boolean>{

  val signalStrengthLevels: MutableList<Boolean> = mutableListOf(false,false,false,false,false,false)
  for (i in signalStrengthLevels.indices){
    if(signalStrengthLevelPercent[i]<=sst) signalStrengthLevels[i]=true
  }
  return signalStrengthLevels
}

private fun SignalIndicatorColor(ssth:Float):Color{
  return if(ssth<0.05f) Color.Gray else{
    if(ssth<0.25f) Color.Red else {
      if(ssth<=0.5f) Color(0xFFB8860B) else Color.Green
    }
  }
}