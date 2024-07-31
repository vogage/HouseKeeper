package io.getstream.webrtc.sample.compose.car.Camera

import android.graphics.SurfaceTexture
import kotlinx.coroutines.flow.StateFlow

interface CameraManger {
  val cameraState: StateFlow<CameraState>
  fun initial()

  fun openCamera(p0: SurfaceTexture, width: Int, height: Int, onError:(Exception)->Unit)

  fun closeCamera(onError:(Exception)->Unit)
}


data class CameraState(
  var msg:String=""
)