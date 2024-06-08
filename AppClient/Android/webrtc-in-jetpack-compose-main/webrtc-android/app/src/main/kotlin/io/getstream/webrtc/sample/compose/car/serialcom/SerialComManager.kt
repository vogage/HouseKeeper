package io.getstream.webrtc.sample.compose.car.serialcom

import com.hoho.android.usbserial.driver.UsbSerialPort
import kotlinx.coroutines.flow.StateFlow


interface SerialComManager{

  val serialComStateflow:StateFlow<SerialComState>
  val availableSerialItemsFlow:StateFlow<List<DeviceItem>>
  fun initial()
  fun start()

  fun updateUsbAvailableDrivers()
  fun connect()
  fun selectSerialItem(id:Int)
  fun close()

  fun send()

  fun listen()
}

enum class UsbPermission {
  Unknown,
  Requested,
  Granted,
  Denied
}

enum class SerialComState {
  Active, // Offer and Answer messages has been sent
  Creating, // Creating session, offer has been sent
  UsbAttached,// new usb device attached to the phone
  Permitted,//the extra usb device visit is permitted
  Denied,// the visit permission was denied
  Disable,// the port cannot be connected
  Ready, // Both clients available and ready to initiate session
  Impossible, // We have less than two clients connected to the server
  Offline // unable to connect signaling server
}
data class DeviceItem(
  var bauRate: Int = 9600,
  var stopBits:Int = 1,
  var dataBits:Int = 8,
  var parity:Int = UsbSerialPort.PARITY_NONE,
  val vendorId:Int=-1,
  val productId:Int=-1,
  val idOfItem:Int=-1,
){
  fun setConfig(bauRate: Int,stopBits: Int,dataBits: Int,port: Int){
    this.bauRate=bauRate
    this.stopBits=stopBits
    this.dataBits=dataBits
    this.parity=port
  }
}