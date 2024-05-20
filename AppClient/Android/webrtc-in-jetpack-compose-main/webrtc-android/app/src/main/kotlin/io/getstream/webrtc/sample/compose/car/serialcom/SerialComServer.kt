package io.getstream.webrtc.sample.compose.car.serialcom

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import io.getstream.log.taggedLogger
import io.getstream.webrtc.sample.compose.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class SerialComServer(
  private val context: Context
){
  private val logger by taggedLogger("Call:SerivalComServer")
  private val carOnLine: Boolean=false;
  private var broadcastReceiver: BroadcastReceiver? = null
  private val INTENT_ACTION_GRANT_USB: String = BuildConfig.APPLICATION_ID + ".GRANT_USB"
  private var usbPermission: UsbPermission = UsbPermission.Unknown
  private var usbSerialPort: UsbSerialPort? = null
  private val withIoManager = false
  private lateinit var usbManager: UsbManager
  private var usbIoManager: SerialInputOutputManager? = null

  private val _SerialComStateFlow = MutableStateFlow(SerialComState.Creating)
  val serialcomstateflow: StateFlow<SerialComState> = _SerialComStateFlow

  private val deviceId = 0
  private var portNum:kotlin.Int = 0
  private var baudRate:kotlin.Int = 9600

  private enum class UsbPermission {
    Unknown,
    Requested,
    Granted,
    Denied
  }

  fun start() {
    broadcastReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        if (INTENT_ACTION_GRANT_USB.equals(intent.action)) {
          usbPermission = if (intent.getBooleanExtra(
              UsbManager.EXTRA_PERMISSION_GRANTED,
              false
            )
          ) UsbPermission.Granted else UsbPermission.Denied
          connect()
        }
      }
    }
   // ContextCompat.registerReceiver(context, broadcastReceiver,
   //   new IntentFilter(INTENT_ACTION_GRANT_USB), ContextCompat.RECEIVER_NOT_EXPORTED);


  }

  /*
     * Serial + UI
     */

  private fun connect() {
    logger.d{"Call:SerivalComServer connect()"}
    var device: UsbDevice? = null
    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    for (v in usbManager.deviceList.values) if (v.deviceId == deviceId) device = v

    var driver = UsbSerialProber.getDefaultProber().probeDevice(device)
    if (driver.ports.size < portNum) {
      return
    }
    usbSerialPort = driver.ports[portNum]
    val usbConnection = usbManager.openDevice(driver.device)
    if (usbConnection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(
        driver.device
      )
    ) {
      usbPermission = UsbPermission.Requested
      val flags =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_MUTABLE else 0
      val intent = Intent(INTENT_ACTION_GRANT_USB)

      val usbPermissionIntent = PendingIntent.getBroadcast(context, 0, intent, flags)
      if(usbPermissionIntent==null){

      }else{

      }
      usbManager.requestPermission(driver.device, usbPermissionIntent)
      return
    }

    try {
     usbSerialPort?.open(usbConnection)
      try {
        usbSerialPort?.setParameters(baudRate, 8, 1, UsbSerialPort.PARITY_NONE)
      } catch (e: UnsupportedOperationException) {

      }
      if (withIoManager) {
        usbIoManager = SerialInputOutputManager(usbSerialPort, MySerialListener)
        usbIoManager!!.start()
      }


    } catch (e: Exception) {




    }
  }

  companion object MySerialListener:SerialInputOutputManager.Listener {
    override fun onNewData(data: ByteArray?) {

    }

    override fun onRunError(e: java.lang.Exception?) {

    }
  }


}

enum class SerialComState {
  Active, // Offer and Answer messages has been sent
  Creating, // Creating session, offer has been sent
  Ready, // Both clients available and ready to initiate session
  Impossible, // We have less than two clients connected to the server
  Offline // unable to connect signaling server
}