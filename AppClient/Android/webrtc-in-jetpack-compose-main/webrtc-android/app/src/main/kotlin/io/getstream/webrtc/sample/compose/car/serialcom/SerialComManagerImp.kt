package io.getstream.webrtc.sample.compose.car.serialcom

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
  private val logger by taggedLogger("Call:SerialComManagerImp")
  private var mUsbBroadcastReceiver: BroadcastReceiver? = null
  private val MY_INTENT_ACTION_PERMISSION_USB:String = BuildConfig.APPLICATION_ID+ "USB_PERMISSION"
  private var usbPermission: UsbPermission = UsbPermission.Unknown
  private var usbSerialPort: UsbSerialPort? = null
  private var usbDriver:UsbSerialDriver?=null
  private val withIoManager = true
  private lateinit var usbIoManager: SerialInputOutputManager
  private var _availableDevices:MutableList<DeviceItemImp> = mutableListOf()

  private lateinit var theSelectedSerialPortItem:DeviceItemImp
  private var receivedSerialComData:String = "No msg"
// the flow used for serial communication config ui
  private var _serialState= MutableStateFlow(SerialComState.Closed)
  override val serialState: StateFlow<SerialComState> =_serialState

  private lateinit var serialData:SerialData
  private lateinit var usbManager: UsbManager


  private var errorMsg:String=""
  private var isInitialized = false

  private class MySerialListener(
    var _serialState:MutableStateFlow<SerialComState>,
    var revData:String,
    var errData:String):SerialInputOutputManager.Listener{
    override fun onNewData(data: ByteArray?) {
      revData = data.toString()
    }

    override fun onRunError(e: java.lang.Exception?) {
      _serialState.update { SerialComState.Error }
      errData = e.toString()
    }
  }
  private data class DeviceItemImp(
    val driver: UsbSerialDriver,
    val item:DeviceItem=DeviceItem(idOfItem=-1)
  ): DeviceItem()
  private fun toDeviceItemList(mutableList: MutableList<DeviceItemImp>):MutableList<DeviceItem>{
    val res:MutableList<DeviceItem> = mutableListOf()
    errorMsg += "toDeviceItemList \n"
    mutableList.forEach{ it -> res.add(it.item)}
    return res
  }
  private fun serialDeviceAdapter(newDriver: UsbSerialDriver, idOfItem: Int): DeviceItemImp {
    errorMsg+="serialDeviceAdapter \n"
    return DeviceItemImp(
      driver = newDriver,
      item =DeviceItem(
        bauRate = 9600,
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
    if(isInitialized)return
    val filter =IntentFilter(MY_INTENT_ACTION_PERMISSION_USB)
    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED)
    filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
    filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
    filter.addAction(UsbManager.EXTRA_PERMISSION_GRANTED)
    mUsbBroadcastReceiver=instanceUsbBroadcastReceiver()
    context.registerReceiver(mUsbBroadcastReceiver,filter, Context.RECEIVER_NOT_EXPORTED)
    _serialState.update { SerialComState.Initialized }
    isInitialized=true
  }

  //create a system broadcastReceiver to deal with the event with usb device attached
  private fun instanceUsbBroadcastReceiver():BroadcastReceiver{
    return object: BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        errorMsg+="onReceive(context: Context, intent: Intent) "+"\n"
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
          UsbManager.ACTION_USB_DEVICE_ATTACHED ->{
            synchronized(this) {
              dealWithUsbAttached(intent)
              errorMsg+="ACTION_USB_Device_ATTACHED"
            }
          }
          UsbManager.ACTION_USB_ACCESSORY_DETACHED->{
            synchronized(this) {
              dealWithUsbDetached(intent)
              errorMsg+="ACTION_USB_ACCESSORY_ATTACHED"
            }
          }
          UsbManager.ACTION_USB_DEVICE_DETACHED->{
            synchronized(this) {
              dealWithUsbDetached(intent)
              errorMsg+="ACTION_USB_ACCESSORY_DETACHED"
            }
          }
        }

      }
    }
  }

  private fun dealWithUsbPermissionResult(intent:Intent){
    usbPermission = if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
      errorMsg+=" UsbPermission.Denied" +"\n"
      UsbPermission.Denied
    } else {
      errorMsg+=" UsbPermission.Granted" +"\n"
      try{
         connect()
      }catch (_:Exception){

      }
      UsbPermission.Granted

    }
  }

  private fun dealWithUsbAttached(intent: Intent){
    updateUsbAvailableDrivers()
    //select the default serial port
    //request the permission
    requestPermission()

  }
  private fun dealWithUsbDetached(intent:Intent){
    updateUsbAvailableDrivers()

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
  //  if(_availableDevices.isNotEmpty())
      theSelectedSerialPortItem=_availableDevices[0]
    errorMsg+="theSelectedSerialPortItem=_availableDevices[0] \n"
    errorMsg+=theSelectedSerialPortItem.toString()+"\n"
  }
  override fun getAvailableDevices(): List<DeviceItem> =
    toDeviceItemList(_availableDevices)
  override fun selectSerialItem(id:Int) {
    theSelectedSerialPortItem=_availableDevices[id]

  }
  override fun requestPermission(){
    val permissionIntent = PendingIntent.getBroadcast(context, 0,
      Intent(MY_INTENT_ACTION_PERMISSION_USB), PendingIntent.FLAG_IMMUTABLE)
    usbManager.requestPermission(theSelectedSerialPortItem.driver.device,permissionIntent)
  }
  override fun connect() {
    logger.d{"Call:SerialComServer connect()"}
    errorMsg+="call connect function"+"\n"
    //if(theSelectedSerialPortItem.idOfItem!=-1)
    errorMsg+="theSelectedSerialPortItem.idOfItem!=-1   "+theSelectedSerialPortItem.idOfItem.toString()+"\n"
    //errorMsg+=theSelectedSerialPortItem.driver.toString()+"\n"
    //errorMsg+=theSelectedSerialPortItem.driver.device.toString()+"\n"
      usbDriver=theSelectedSerialPortItem.driver
    if(usbDriver==null){
      errorMsg+="usbDriver==null"+"\n"
      return }
    else{
      errorMsg+= "usbManager $usbManager \n"
      errorMsg+="---------------------------------------------------------"+"\n"
      errorMsg+="haspermisson ?   "+usbManager.hasPermission(theSelectedSerialPortItem.driver.device).toString()+"\n"
     // val usbConnection = usbManager.openDevice(usbDriver!!.device)

        val usbConnection = usbManager.openDevice(theSelectedSerialPortItem.driver.device)

        errorMsg+="call usbManager.openDevice()"+"\n"
        //errorMsg+="theSelectedSerialPortItem.driver.device"+theSelectedSerialPortItem.driver.device.toString()+"\n"
        errorMsg+= "usbConnection   $usbConnection   \n"

        usbSerialPort=theSelectedSerialPortItem.driver.ports[0]
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
          _serialState.update { SerialComState.Working }
        } catch (e: UnsupportedOperationException) {
          errorMsg+="usbSerialPort?.setParameters "+e.message.toString()

        }
        if (withIoManager) {
          usbIoManager = SerialInputOutputManager(
            usbSerialPort,
            MySerialListener(_serialState,receivedSerialComData,errorMsg))
          errorMsg+=" usbIoManager = SerialInputOutputManager \n"
        }
      } catch (e: Exception) {
        errorMsg+=" usbSerialPort?.open(usbConnection) "+e.message.toString()+"\n"

      }
    }
  }

  override fun testSerial() {
    val text = byteArrayOf(0xAA.toByte(),0x00.toByte(),0x01.toByte(), 0x00.toByte(),0x00.toByte(),0x00.toByte(),0x00.toByte(),0xAB.toByte())
    errorMsg+= "usbSerialPort?.write(text,500) $text \n"
    try {
      usbSerialPort?.write(text, 500)
    }catch (e:Exception){
      errorMsg+=e.toString()+"\n"
    }
  }

  override fun listen() {
    usbIoManager.start()
  }

  override fun close() {
    usbIoManager.stop()
    context.unregisterReceiver(mUsbBroadcastReceiver)
  }

  private fun testSend(text:ByteArray){

    try {
      usbIoManager.start()
      usbIoManager.writeAsync(text)
      errorMsg+= "usbIoManager.writeAsync(text) $text \n"
    }catch (e:Exception){
      errorMsg+= "usbIoManager.writeAsync(text)  $e\n"
    }
  }
  override fun send(str:String) {
    _serialState.update { SerialComState.Working }
    if(theSelectedSerialPortItem.idOfItem==-1) {
      errorMsg += "theSelectedSerialPortItem.driver==null \n"
      return
    }
    errorMsg += "theSelectedSerialPortItem.idOfItem:  "+theSelectedSerialPortItem.idOfItem+"\n"
    _serialState.update { SerialComState.Error }
    usbIoManager.writeAsync(str.toByteArray())
  }


  override fun getReceivedMsg(): String = receivedSerialComData

}


