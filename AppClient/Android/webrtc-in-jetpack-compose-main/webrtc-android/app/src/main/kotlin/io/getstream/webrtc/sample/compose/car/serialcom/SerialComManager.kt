package io.getstream.webrtc.sample.compose.car.serialcom

import kotlinx.coroutines.flow.StateFlow


interface SerialComManager{

  val serialcomstateflow:StateFlow<SerialComState>

  fun start()

  fun connect()

  fun close()

  fun send()


}

enum class UsbPermission {
  Unknown,
  Requested,
  Granted,
  Denied
}
data class serialcomConfig(
  private val deviceId:Int = 0,
  private var portNum:Int = 0 ,
  private var baudRate:Int = 9600
)

enum class SerialComState {
  Active, // Offer and Answer messages has been sent
  Creating, // Creating session, offer has been sent
  UsbAttached,// new usb device attached to the phone
  Ready, // Both clients available and ready to initiate session
  Impossible, // We have less than two clients connected to the server
  Offline // unable to connect signaling server
}