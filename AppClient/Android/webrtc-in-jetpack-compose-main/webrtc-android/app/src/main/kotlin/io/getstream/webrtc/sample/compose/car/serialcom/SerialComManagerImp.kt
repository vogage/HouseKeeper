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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map


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
  private var _availableDevices:MutableList<DeviceItemImp> = mutableListOf()
  private var availableSerialPortsFlow: Flow<MutableList<DeviceItemImp>>  = flow{ emit(mutableListOf())}

  override val availableDevicesFlow: Flow<List<DeviceItem>> = availableSerialPortsFlow.map{toDeviceItemList(it).toList()}

  private data class DeviceItemImp(
    val driver:UsbSerialDriver,
    val item:DeviceItem=DeviceItem()
  ): DeviceItem()
  private fun toDeviceItemList(mutableList: MutableList<DeviceItemImp>):MutableList<DeviceItem>{
    val res:MutableList<DeviceItem> = mutableListOf()
    mutableList.forEach{it -> res.add(it.item)}
    return res
  }
  private fun serialDeviceAdapter(driver: UsbSerialDriver, idOfItem: Int): DeviceItemImp {
    return DeviceItemImp(
      driver=driver,
      item=DeviceItem(
        bauRate = 9600,
        stopBits = 1,
        dataBits = 8,
        parity = UsbSerialPort.PARITY_NONE,
        vendorId=driver.device.vendorId,
        productId = driver.device.productId,
        idOfItem=idOfItem
      )
    )
  }

  private var theSelectedSerialPortItem:DeviceItemImp?=null
// the flow used for serial communication config ui


  private lateinit var usbManager: UsbManager



  //register an intent filter listen for usb device attach
  override fun initial() {
    logger.d{"initial"}
    val filter:IntentFilter =IntentFilter(MY_INTENT_ACTION_PERMISSION_USB)
    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED)
    mUsbBroadcastReceiver=instanceUsbBroadcastReceiver()
    context.registerReceiver(mUsbBroadcastReceiver,filter)
  }

  //create a system broadcastReceiver to deal with the event with usb device attached
  private fun instanceUsbBroadcastReceiver():BroadcastReceiver{
    return object: BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {

        when(intent.action){
          MY_INTENT_ACTION_PERMISSION_USB ->{//request user to visit the usb device
            synchronized(this) {
              dealWithUsbPermissionResult(intent)
            }
          }
          UsbManager.ACTION_USB_ACCESSORY_ATTACHED ->{
            synchronized(this) {
              dealWithUsbAttached(intent)
            }
          }
          UsbManager.ACTION_USB_ACCESSORY_DETACHED->{
            synchronized(this) {
              dealWithUsbDettached(intent)
            }
          }
        }

      }
    }
  }

  private fun dealWithUsbPermissionResult(intent:Intent){
    if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false)){
      usbPermission=UsbPermission.Granted

    }else{
      usbPermission=UsbPermission.Denied

    }
  }

  private fun dealWithUsbAttached(intent: Intent){
    updateUsbAvailableDrivers()
  }
  private fun dealWithUsbDettached(intent:Intent){
    updateUsbAvailableDrivers()
  }

  override fun updateUsbAvailableDrivers(){
    usbManager=context.getSystemService(Context.USB_SERVICE) as UsbManager
    val usbDefaultProber = UsbSerialProber.getDefaultProber()
    //val usbCustomProber: UsbSerialProber = CustomProber.getCustomProber()// to probe the specific serial device
    _availableDevices.clear()
    for ((idOfItem, device) in usbManager.deviceList.values.withIndex()) {
      val driver = usbDefaultProber.probeDevice(device)
      _availableDevices.add(serialDeviceAdapter(driver,idOfItem))
      if(theSelectedSerialPortItem==null) theSelectedSerialPortItem= _availableDevices[0]
    }

  }

  override fun selectSerialItem(id:Int) {
    _availableDevices[id]
    //theSelectedSerialPortItem.usbSerialDriver=
  }
  override fun connect() {
    logger.d{"Call:SerialComServer connect()"}
    if(usbDriver==null){

      return
    }else{
      val usbConnection = usbManager.openDevice(usbDriver!!.device)
      try {
        usbSerialPort?.open(usbConnection)
        try {
          theSelectedSerialPortItem?.let {
            usbSerialPort?.setParameters(
              it.bauRate,
              it.dataBits,
              it.stopBits,
              it.parity
            )
          }
        } catch (e: UnsupportedOperationException) {

        }
        if (withIoManager) {
          usbIoManager = SerialInputOutputManager(usbSerialPort, MySerialListener)
          usbIoManager!!.start()
        }


      } catch (e: Exception) {




      }
    }


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

  override fun listen() {
    TODO("Not yet implemented")
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


