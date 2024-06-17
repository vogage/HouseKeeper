package io.getstream.webrtc.sample.compose.car

import io.getstream.webrtc.sample.compose.car.serialcom.SerialComManager

interface CarManager {
  val serialCom: SerialComManager
  fun carReady()




}