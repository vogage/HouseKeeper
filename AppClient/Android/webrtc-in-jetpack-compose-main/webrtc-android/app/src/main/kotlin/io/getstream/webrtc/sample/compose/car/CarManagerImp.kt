package io.getstream.webrtc.sample.compose.car

import android.content.Context
import io.getstream.webrtc.sample.compose.car.serialcom.SerialComServer


class CarManagerImp(
  private val context: Context,
  val serialcomserver: SerialComServer,
):CarManager{
  override fun carReady() {
    serialcomserver.start()
  }

}