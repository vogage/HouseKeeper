package io.getstream.webrtc.sample.compose.car.Camera

import kotlinx.coroutines.flow.StateFlow

interface CameraManger {
  val cameraState: StateFlow<CameraState>
  fun initial()
}


data class CameraState(
  var msg:String=""
)