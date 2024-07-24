package io.getstream.webrtc.sample.compose.car

import android.content.Context
import io.getstream.log.taggedLogger
import io.getstream.webrtc.sample.compose.car.Camera.CameraManger
import io.getstream.webrtc.sample.compose.car.serialcom.SerialComManager

class CarManagerImp(
  private val context: Context,
  override val serialCom: SerialComManager,
  override val carCamera: CameraManger
):CarManager{

  override fun carReady() {
    val logger by taggedLogger("Call:CarReady")
    logger.d{"hfadsofjaofjawdlkgfjaljadlfkjadslkfjadsflkjasdklfjnadslkfjasdlkfjadslkfjnasdlkfjasdlkfjalsdkfjalskdfjklasde"}

  }

}