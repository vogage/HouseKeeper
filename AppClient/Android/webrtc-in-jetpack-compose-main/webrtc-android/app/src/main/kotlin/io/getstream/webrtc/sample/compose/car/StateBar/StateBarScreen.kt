package io.getstream.webrtc.sample.compose.car.StateBar

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun StateBarScreen(
  viewModel: MyStateBarViewModel
){
  Box(
     contentAlignment = Alignment.Center
  ) {

    CarStateBar1()
  }

}