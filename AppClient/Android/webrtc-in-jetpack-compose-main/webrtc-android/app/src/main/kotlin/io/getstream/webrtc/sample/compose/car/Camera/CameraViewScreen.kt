package io.getstream.webrtc.sample.compose.car.Camera

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun CameraViewScreen(
viewModel: CameraViewModel
){
Column {
  Button(
    onClick = {viewModel.initial()}
  ){
    Text("initial Camera")
  }
}
}


