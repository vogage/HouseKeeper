package io.getstream.webrtc.sample.compose.car.StateBar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StateBarScreen(
  viewModel: CarStateBarViewModel
){

  Box(
     contentAlignment = Alignment.Center
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        CarStatusBar(viewModel)
      }
      Spacer(Modifier.height(100.dp))
      SliderOfSignalStrength(OnchangePosition = { viewModel.changeSignalStrength(it)})
      Spacer(Modifier.height(100.dp))
      Column {
        Spacer(modifier = Modifier.height(100.dp))
        SliderOfBatteryVolume(OnchangePosition ={ viewModel.changeVolume(it)})
      }
    }


  }

}


