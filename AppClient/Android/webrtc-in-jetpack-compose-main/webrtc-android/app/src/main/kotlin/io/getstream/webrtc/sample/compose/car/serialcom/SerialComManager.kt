package io.getstream.webrtc.sample.compose.car.serialcom

import io.getstream.webrtc.sample.compose.BuildConfig
import kotlinx.coroutines.flow.StateFlow


interface SerialComManager{

  val serialcomstateflow:StateFlow<SerialComState>

  fun initial()
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
data class SerialComConfig(
  val deviceId:Int = 0,
  var portNum:Int = 0 ,
  var baudRate:Int = 9600,
  val ACTION_USB_PERMISSION:String = BuildConfig.APPLICATION_ID+ "USB_PERMISSION"
)

enum class SerialComState {
  Active, // Offer and Answer messages has been sent
  Creating, // Creating session, offer has been sent
  UsbAttached,// new usb device attached to the phone
  Permitted,//the extra usb device visit is permitted
  Ready, // Both clients available and ready to initiate session
  Impossible, // We have less than two clients connected to the server
  Offline // unable to connect signaling server
}