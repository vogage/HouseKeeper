package io.getstream.webrtc.sample.compose.car.JoyStick

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun JoyStickScreen(
  joyStickViewModel: MyJoyStickViewModel
){
  Column {
    Box(
      contentAlignment = Alignment.Center) {
      MyJoyStick(joyStickViewModel)
    }

  }

}