package io.getstream.webrtc.sample.compose.car.serialcom

import android.content.BroadcastReceiver
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.UsbSerialPort

import com.hoho.android.usbserial.util.SerialInputOutputManager





internal class SerialComAdapterImpl(

):SerialComAdapter{



  private val broadcastReceiver: BroadcastReceiver? = null
  val usbManager:UsbManager?=null
  private val usbIoManager: SerialInputOutputManager? = null
  private val usbSerialPort: UsbSerialPort? = null
  private val usbPermission: UsbPermission = UsbPermission.Unknown
  private val connected = false


  private enum class UsbPermission {
    Unknown,
    Requested,
    Granted,
    Denied
  }

  override fun init() {
    val deviceId:Int =112233;
    val portNum:Int =20;
    val baudRate:Int = 9600;

  }

  override fun open() {
    TODO("Not yet implemented")
  }

  override fun connect() {
    TODO("Not yet implemented")
  }
  override fun send() {
    TODO("Not yet implemented")
  }

  override fun read() {
    TODO("Not yet implemented")
  }

  override fun close() {
    TODO("Not yet implemented")
  }
}


