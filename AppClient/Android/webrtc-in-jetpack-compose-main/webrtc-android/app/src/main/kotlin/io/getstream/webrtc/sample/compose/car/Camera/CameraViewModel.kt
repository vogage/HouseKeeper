package io.getstream.webrtc.sample.compose.car.Camera

import androidx.lifecycle.ViewModel

class CameraViewModel(
  private val carCamera:CameraManger
): ViewModel() {
  fun initial(){
    carCamera.initial()
  }
}