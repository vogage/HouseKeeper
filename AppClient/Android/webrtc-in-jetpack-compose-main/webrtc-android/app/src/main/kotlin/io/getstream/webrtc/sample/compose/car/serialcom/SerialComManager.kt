package io.getstream.webrtc.sample.compose.car.serialcom

import com.hoho.android.usbserial.driver.UsbSerialPort
import kotlinx.coroutines.flow.StateFlow


interface SerialComManager{
  val serialState: StateFlow<SerialComState>

  fun getAvailableDevices():List<DeviceItem>
  fun getSelectedDevice():DeviceItem
  fun initial()
  fun updateUsbAvailableDrivers()
  fun connect()
  fun selectSerialItem(id:Int)
  fun close()
  fun send(str:String)
  fun listen()
  fun getErrorMsg():String

  fun getReceivedMsg():String
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
  Initialized, //
  Selected, // the serial port running
  Connected, // Both clients available and ready to initiate session
  Working,// the serial port is in working
  Received,//the msg received
  UsbAttached,// new usb device attached to the phone
  UsbDetached, // usb device detached to the phone
  Disable,// the port cannot be connected
  Closed, // the serialCom was closed
  Error// some go wrong
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