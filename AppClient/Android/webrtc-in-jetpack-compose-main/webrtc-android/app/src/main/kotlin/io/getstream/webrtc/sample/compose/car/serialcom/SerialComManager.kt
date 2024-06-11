package io.getstream.webrtc.sample.compose.car.serialcom

import com.hoho.android.usbserial.driver.UsbSerialPort
import kotlinx.coroutines.flow.Flow


interface SerialComManager{
  val availableDevicesFlow: Flow<List<DeviceItem>>
  fun initial()
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
data class SerialData(
  var state: SerialComState=SerialComState.Disable,  //serialComState
  var msg:String="" // additional message
){

}
enum class SerialComState {
  Creating, // Creating session, offer has been sent
  Active, // the serial port running
  Ready, // Both clients available and ready to initiate session
  UsbAttached,// new usb device attached to the phone
  UsbDetached, // usb device detached to the phone
  Disable,// the port cannot be connected
  Closed // the serialCom was closed
}

open class DeviceItem(
  var bauRate: Int = 9600,
  var stopBits:Int = 1,
  var dataBits:Int = 8,
  var parity:Int = UsbSerialPort.PARITY_NONE,
  val vendorId:Int=-1,
  val productId:Int=-1,
  val idOfItem:Int=-1,
){
  fun setConfig(
    bauRate: Int= this.bauRate,
    stopBits: Int=this.stopBits,
    dataBits: Int= this.dataBits,
    port: Int= this.parity,
   ){}// vendorId,productId,idOfItem can not be set
}