package io.getstream.webrtc.sample.compose.car.StateBar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StateBarScreen(
  viewModel: MyStateBarViewModel
){
  Box(
     contentAlignment = Alignment.Center
  ) {

    CarStateBar1(viewModel)
    Column {
      Spacer(modifier = Modifier.height(100.dp))
      SliderOfBatteryValume(OnchangePosition ={it-> viewModel.changeVolume(it)})
    }

  }

}


@Composable
fun SliderOfBatteryValume(
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