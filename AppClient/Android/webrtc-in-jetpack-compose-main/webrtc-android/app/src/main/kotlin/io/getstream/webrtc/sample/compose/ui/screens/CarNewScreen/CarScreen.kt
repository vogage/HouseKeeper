package io.getstream.webrtc.sample.compose.ui.screens.CarNewScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.getstream.webrtc.sample.compose.car.serialcom.SerialComState


@Composable
fun CarScreen(
  comstate:SerialComState,
  onComCreate: () -> Unit
){

  Box(
    modifier = Modifier.fillMaxSize()
  ) {
    Text(
      text = "Serial HHHHH"
    )
    Button(onClick = { onComCreate.invoke() }) {
      Text("sssstttarrt")
    }
  }


}
