package io.getstream.webrtc.sample.compose.car.serialcom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import io.getstream.log.taggedLogger
import io.getstream.webrtc.sample.compose.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


class SerialComManagerImp(private val context:Context):SerialComManager{
  private val logger by taggedLogger("Call:SerialComServer")
  private var mUsbBroadcastReceiver: BroadcastReceiver? = null
  private val MY_INTENT_ACTION_GRANT_USB: String = BuildConfig.APPLICATION_ID + ".GRANT_USB"
  private val MY_INTENT_ACTION_PERMISSION_USB:String = BuildConfig.APPLICATION_ID+ "USB_PERMISSION"
  private var usbPermission: UsbPermission = UsbPermission.Unknown
  private var usbSerialPort: UsbSerialPort? = null
  private var usbDriver:UsbSerialDriver?=null
  lateinit var device: UsbDevice
  private val withIoManager = true
  private var isInitialized = false
  private lateinit var usbIoManager: SerialInputOutputManager
  private var _availableDevices:MutableList<DeviceItemImp> = mutableListOf()

  private lateinit var theSelectedSerialPortItem:DeviceItemImp
  private var receivedSerialComData:String = "No msg"
// the flow used for serial communication config ui
  private var _serialState= MutableStateFlow(SerialComState.Disable)
  override val serialState: StateFlow<SerialComState> =_serialState

  private lateinit var usbManager: UsbManager


  private var errorMsg:String=""

  private class MySerialListener(
    var _serialState:MutableStateFlow<SerialComState>,
    var revData:String,
    var errData:String):SerialInputOutputManager.Listener{
    override fun onNewData(data: ByteArray?) {
      _serialState.update { SerialComState.Received }
      revData = data.toString()
    }

    override fun onRunError(e: java.lang.Exception?) {
      _serialState.update { SerialComState.Error }
      errData = e.toString()
    }
  }
  private data class DeviceItemImp(
    var driver: UsbSerialDriver,
    var item:DeviceItem=DeviceItem(idOfItem=-1)
  ): DeviceItem()
  private fun toDeviceItemList(mutableList: MutableList<DeviceItemImp>):MutableList<DeviceItem>{
    val res:MutableList<DeviceItem> = mutableListOf()
    errorMsg += "toDeviceItemList \n"
    mutableList.forEach{ it -> res.add(it.item)}
    return res
  }
  private fun serialDeviceAdapter(newDriver: UsbSerialDriver, idOfItem: Int): DeviceItemImp {
    return DeviceItemImp(
      driver =newDriver,
      item =DeviceItem(
        bauRate = 19200,
        stopBits = 1,
        dataBits = 8,
        parity = UsbSerialPort.PARITY_NONE,
        vendorId=newDriver.device.vendorId,
        productId = newDriver.device.productId,
        idOfItem=idOfItem
      )
    )
  }


  override fun getErrorMsg(): String = errorMsg
  override fun getSelectedDevice(): DeviceItem = theSelectedSerialPortItem.item
  //register an intent filter listen for usb device attach
  override fun clearMsg() {
    errorMsg=""
  }
  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  override fun initial() {
    logger.d{"initial"}

    val filter =IntentFilter(MY_INTENT_ACTION_PERMISSION_USB)
    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED)
    filter.addAction(UsbManager.EXTRA_PERMISSION_GRANTED)
    mUsbBroadcastReceiver=instanceUsbBroadcastReceiver()
    context.registerReceiver(mUsbBroadcastReceiver,filter, Context.RECEIVER_NOT_EXPORTED)
    _serialState.update { SerialComState.Initialized }
    errorMsg="initial"
    isInitialized = true
  }

  //create a system broadcastReceiver to deal with the event with usb device attached
  private fun instanceUsbBroadcastReceiver():BroadcastReceiver{
    return object: BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        when(intent.action){
          MY_INTENT_ACTION_PERMISSION_USB ->{//request user to visit the usb device
            synchronized(this) {
              dealWithUsbPermissionResult(intent)
              errorMsg+="MY_INTENT_ACTION_PERMISSION_USB"
            }
          }
          UsbManager.ACTION_USB_ACCESSORY_ATTACHED ->{
            synchronized(this) {
              dealWithUsbAttached(intent)
              errorMsg+="ACTION_USB_ACCESSORY_ATTACHED"
            }
          }
          UsbManager.ACTION_USB_ACCESSORY_DETACHED->{
            synchronized(this) {
              dealWithUsbDetached(intent)
              errorMsg+="ACTION_USB_ACCESSORY_ATTACHED"
            }
          }
        }

      }
    }
  }

  private fun dealWithUsbPermissionResult(intent:Intent){
    usbPermission = if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
      UsbPermission.Granted
    } else {
      UsbPermission.Denied
    }
  }

  private fun dealWithUsbAttached(intent: Intent){
    updateUsbAvailableDrivers()
    _serialState.update { SerialComState.UsbAttached }
  }
  private fun dealWithUsbDetached(intent:Intent){
    updateUsbAvailableDrivers()
    _serialState.update { SerialComState.UsbDetached }
  }

  override fun updateUsbAvailableDrivers(){
//    if(usbPermission != UsbPermission.Granted) {
//      errorMsg+=usbPermission.toString()+"\n"
//      return
//    }
    val usbDefaultProber = UsbSerialProber.getDefaultProber()
    //val usbCustomProber: UsbSerialProber = CustomProber.getCustomProber()// to probe the specific serial device
    usbManager=context.getSystemService(Context.USB_SERVICE) as UsbManager
    _availableDevices.clear()
    for ((idOfItem, device) in usbManager.deviceList.values.withIndex()) {
      val driver = usbDefaultProber.probeDevice(device)
      errorMsg += driver?.toString() ?: ("driver == null from prober" + "\n")
      _availableDevices.add(serialDeviceAdapter(driver,idOfItem)) //serialDeviceAdapter(driver: UsbSerialDriver, idOfItem: Int): DeviceItemImp
    }
    if(_availableDevices.isNotEmpty()) theSelectedSerialPortItem=_availableDevices[0]
  }
  override fun getAvailableDevices(): List<DeviceItem> =
    toDeviceItemList(_availableDevices)
  override fun selectSerialItem(id:Int) {
    theSelectedSerialPortItem=_availableDevices[id]
    _serialState.update { SerialComState.Selected }
  }
  override fun connect() {
    logger.d{"Call:SerialComServer connect()"}
    errorMsg+="call connect function"+"\n"
    if(theSelectedSerialPortItem.idOfItem!=-1) usbDriver=theSelectedSerialPortItem.driver
    if(usbDriver==null){
      errorMsg+="usbDriver==null"+"\n"
      return }
    else{
      val usbConnection = usbManager.openDevice(usbDriver!!.device)
      errorMsg+="call usbManager.openDevice(usbDriver!!.device)"+"\n"
      try {
        usbSerialPort?.open(usbConnection)
        try {
          theSelectedSerialPortItem.let {
            usbSerialPort?.setParameters(
              it.bauRate,
              it.dataBits,
              it.stopBits,
              it.parity
            )
          }
          _serialState.update { SerialComState.Connected }
        } catch (e: UnsupportedOperationException) {
          errorMsg+="usbSerialPort?.setParameters "+e.message.toString()

        }
        if (withIoManager) {
          usbIoManager = SerialInputOutputManager(
            usbSerialPort,
            MySerialListener(_serialState,receivedSerialComData,errorMsg))
        }
      } catch (e: Exception) {
        errorMsg+=" usbSerialPort?.open(usbConnection) "+e.message.toString()+"\n"

      }
    }
  }

  override fun listen() {
    usbIoManager.start()
  }

  override fun close() {
    usbIoManager.stop()
    context.unregisterReceiver(mUsbBroadcastReceiver)
  }

  override fun send(str:String) {
    _serialState.update { SerialComState.Working }
    if(theSelectedSerialPortItem.driver==null||theSelectedSerialPortItem.idOfItem==-1) {
      errorMsg += "theSelectedSerialPortItem.driver==null \n"
      return
    }
    errorMsg += "theSelectedSerialPortItem.idOfItem:  "+theSelectedSerialPortItem.idOfItem+"\n"
    _serialState.update { SerialComState.Error }
    usbIoManager.writeAsync(str.toByteArray())
  }


  override fun getReceivedMsg(): String = receivedSerialComData

}


