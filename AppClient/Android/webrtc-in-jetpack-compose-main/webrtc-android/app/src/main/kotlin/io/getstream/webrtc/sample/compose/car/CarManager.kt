package io.getstream.webrtc.sample.compose.car

import io.getstream.webrtc.sample.compose.car.Camera.CameraManger
import io.getstream.webrtc.sample.compose.car.serialcom.SerialComManager

interface CarManager {
  val serialCom: SerialComManager
  val carCamera:CameraManger
  fun carReady()




}