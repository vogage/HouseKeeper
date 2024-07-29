package io.getstream.webrtc.sample.compose.car.Camera

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun CameraViewScreen(
viewModel: CameraViewModel
){
  val uiState by viewModel.cameraUiState.collectAsState()

Column {

  Text(text = uiState.stateMsg)
  Button(
    onClick = {viewModel.initial()}
  ){
    Text("initial Camera")
  }
}
}


