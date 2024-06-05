package io.getstream.webrtc.sample.compose.car.serialcom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import io.getstream.log.taggedLogger
import io.getstream.webrtc.sample.compose.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SerialComManagerImp(private val context:Context):SerialComManager{
  private val logger by taggedLogger("Call:SerialComServer")
  private var mUsbBroadcastReceiver: BroadcastReceiver? = null
  private val MY_INTENT_ACTION_GRANT_USB: String = BuildConfig.APPLICATION_ID + ".GRANT_USB"
  private val MY_INTENT_ACTION_PERMISSION_USB:String = BuildConfig.APPLICATION_ID+ "USB_PERMISSION"
  private var usbPermission: UsbPermission = UsbPermission.Unknown
  private var usbSerialPort: UsbSerialPort? = null
  private var usbDriver:UsbSerialDriver?=null
  private val withIoManager = false

  private var usbIoManager: SerialInputOutputManager? = null

  var availableDevices:MutableList<DeviceItem> = mutableListOf()


  private var _availableSerialItemsFlow= MutableStateFlow<MutableList<DeviceItem>>(mutableListOf())
  override val availableSerialItemsFlow=_availableSerialItemsFlow.asStateFlow()


  private var _SerialComStateFlow = MutableStateFlow(SerialComState.Creating)
  override val serialComStateflow=_SerialComStateFlow.asStateFlow()

  private lateinit var usbManager: UsbManager


  //register an intent filter
  override fun initial() {
    logger.d{"initial"}
    val filter:IntentFilter =IntentFilter(MY_INTENT_ACTION_PERMISSION_USB)
    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED)
    InstanceUsbBroadcastReceiver()
    context.registerReceiver(mUsbBroadcastReceiver,filter)
  }

  //create a system broadcastReceiver to deal with the event with usb device attached
  private fun InstanceUsbBroadcastReceiver(){
    mUsbBroadcastReceiver = object: BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
          MY_INTENT_ACTION_PERMISSION_USB ->{//request user to visit the usb device
            synchronized(this) {
              DealWithUsbPermissionResult(intent)
            }
          }
          UsbManager.ACTION_USB_ACCESSORY_ATTACHED ->{
            synchronized(this) {
              DealWithUsbAttached(intent)
            }
          }
          UsbManager.ACTION_USB_ACCESSORY_DETACHED->{
            synchronized(this) {
              DealWithUsbDettached(intent)
            }
          }
        }

      }
    }
  }

  private fun DealWithUsbPermissionResult(intent:Intent){
    if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false)){
      usbPermission=UsbPermission.Granted
      _SerialComStateFlow.update { SerialComState.Permitted }
    }else{
      usbPermission=UsbPermission.Denied
      _SerialComStateFlow.update { SerialComState.Denied }
    }
  }

  private fun DealWithUsbAttached(intent: Intent){
    updateUsbAvailableDrivers()
  }
  private fun DealWithUsbDettached(intent:Intent){
    updateUsbAvailableDrivers()
  }

  private fun updateUsbAvailableDrivers(){
    usbManager=context.getSystemService(Context.USB_SERVICE) as UsbManager
    val usbDefaultProber = UsbSerialProber.getDefaultProber()
    //val usbCustomProber: UsbSerialProber = CustomProber.getCustomProber()
    availableDevices.clear()
    var idOfItem:Int=0;
    for (device in usbManager.deviceList.values) {
      val driver = usbDefaultProber.probeDevice(device)
      availableDevices.add(SerialDeviceAdapter(driver,idOfItem))
      idOfItem++
    }
     _availableSerialItemsFlow.update{availableDevices}
  }

  private fun SerialDeviceAdapter(driver:UsbSerialDriver,idOfItem:Int):DeviceItem{
    var item:DeviceItem= DeviceItem(9600,1,8, UsbSerialPort.PARITY_NONE,
      driver.device.vendorId,
      driver.device.productId,
      idOfItem
    )
    return item
  }

  override fun start() {

  }

  override fun connect() {
    logger.d{"Call:SerialComServer connect()"}
//    if(usbDriver==null){
//      updateSerialComState(SerialComState.Disable)
//      return
//    }else{
//      val usbConnection = usbManager.openDevice(usbDriver!!.device)
//    }
//    var device: UsbDevice? = null
//    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
//    for (v in usbManager.deviceList.values) if (v.deviceId == serialComConfig.deviceId) device = v
//
//    var driver = UsbSerialProber.getDefaultProber().probeDevice(device)
//    if (driver.ports.size < serialComConfig.portNum) {
//      return
//    }
//    usbSerialPort = driver.ports[serialComConfig.portNum]
//    val usbConnection = usbManager.openDevice(driver.device)
//
//    try {
//      usbSerialPort?.open(usbConnection)
//      try {
//        usbSerialPort?.setParameters(serialComConfig.baudRate, 8, 1, UsbSerialPort.PARITY_NONE)
//      } catch (e: UnsupportedOperationException) {
//
//      }
//      if (withIoManager) {
//        usbIoManager = SerialInputOutputManager(usbSerialPort, MySerialListener)
//        usbIoManager!!.start()
//      }
//
//
//    } catch (e: Exception) {
//
//
//
//
//    }
//    if (usbConnection == null && usbPermission ==UsbPermission.Unknown && !usbManager.hasPermission(
//        driver.device
//      )
//    ) {
//      usbPermission =UsbPermission.Requested
//      val flags =
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_MUTABLE else 0
//      val intent = Intent(MY_INTENT_ACTION_GRANT_USB)
//
//      val usbPermissionIntent = PendingIntent.getBroadcast(context, 0, intent, flags)
//      if(usbPermissionIntent==null){
//
//      }else{
//
//      }
//      usbManager.requestPermission(driver.device, usbPermissionIntent)
//      return
//    }

  }

  override fun close() {
    context.unregisterReceiver(mUsbBroadcastReceiver)
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


