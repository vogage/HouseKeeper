package io.getstream.webrtc.sample.compose.car

import io.getstream.webrtc.sample.compose.car.serialcom.SerialComServer

interface CarManager {
  val serialcomserver: SerialComServer
  fun carReady()




}