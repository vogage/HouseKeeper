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

class SerialComManagerImp(private val context:Context):SerialComManager{
  private val logger by taggedLogger("Call:SerivalComServer")
  private var mUsbBroadcastReceiver: BroadcastReceiver? = null
  private val INTENT_ACTION_GRANT_USB: String = BuildConfig.APPLICATION_ID + ".GRANT_USB"
  private var usbPermission: UsbPermission = UsbPermission.Unknown
  private var usbSerialPort: UsbSerialPort? = null
  private val withIoManager = false

  private var usbIoManager: SerialInputOutputManager? = null

  private var _SerialComStateFlow = MutableStateFlow(SerialComState.Creating)
  override val serialcomstateflow: StateFlow<SerialComState>
    get() =  _SerialComStateFlow

  private fun updateSerialComState(serialComState:SerialComState){
    _SerialComStateFlow.value=serialComState
  }

  private sealed class UsbIntent() {
    val Attached:String ="android.hardware.usb.action.USB_DEVICE_ATTACHED"

  }
  private lateinit var usbManager: UsbManager

 //begin and reactive to the usbDevice attached
  override fun start() {
    mUsbBroadcastReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        if (INTENT_ACTION_GRANT_USB.equals(intent.action)) {
          usbPermission = if (intent.getBooleanExtra(
              UsbManager.EXTRA_PERMISSION_GRANTED,
              false
            )
          )SerialComManager.UsbPermission.Granted else SerialComManager.UsbPermission.Denied
          connect()
        }
      }

    }
    // ContextCompat.registerReceiver(context, broadcastReceiver,
    //   new IntentFilter(INTENT_ACTION_GRANT_USB), ContextCompat.RECEIVER_NOT_EXPORTED);
    updateSerialComState(SerialComState.UsbAttached)

  }

  /*
     * Serial + UI
     */

  override fun connect() {
    updateSerialComState(SerialComState.Active)
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
    if (usbConnection == null && usbPermission ==UsbPermission.Unknown && !usbManager.hasPermission(
        driver.device
      )
    ) {
      usbPermission =UsbPermission.Requested
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

  override fun close() {
    TODO("Not yet implemented")
  }

  override fun send() {
    TODO("Not yet implemented")
  }
  companion object MySerialListener: SerialInputOutputManager.Listener {
    override fun onNewData(data: ByteArray?) {

    }

    override fun onRunError(e: java.lang.Exception?) {

    }
  }

}


