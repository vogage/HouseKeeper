package io.getstream.webrtc.sample.compose.car.serialcom

//import android.util.Log
//import java.io.File
//import java.io.FileDescriptor
//import java.io.FileInputStream
//import java.io.FileOutputStream
//import java.io.IOException
//import java.io.InputStream
//import java.io.OutputStream
//
//package android_serialport_api
//class SerialPort(
//  device: File,
//  baudrate: Int,
//  stopBits: Int,
//  dataBits: Int,
//  parity: Int,
//  flowCon: Int,
//  flags: Int
//) {
//  private val mFd: FileDescriptor?
//  private var mFileInputStream: FileInputStream? = null
//  private var mFileOutputStream: FileOutputStream? = null
//  val inputStream: InputStream?
//    get() = mFileInputStream
//  val outputStream: OutputStream?
//    get() = mFileOutputStream
//
//  external fun close()
//
//  init {
//    if (!device.canRead() || !device.canWrite()) {
//      try {
//        val su = Runtime.getRuntime().exec("/system/bin/su")
//        val cmd = """
//                chmod 666 ${device.absolutePath}
//                exit
//
//                """.trimIndent()
//        su.outputStream.write(cmd.toByteArray())
//        if (su.waitFor() != 0 || !device.canRead() || !device.canWrite()) {
//          throw SecurityException()
//        }
//      } catch (var10: Exception) {
//        var10.printStackTrace()
//        throw SecurityException()
//      }
//    }
//    mFd = android_serialport_api.SerialPort.Companion.open(
//      device.absolutePath,
//      baudrate,
//      stopBits,
//      dataBits,
//      parity,
//      flowCon,
//      flags
//    )
//    if (mFd == null) {
//      Log.e("SerialPort", "native open returns null")
//      throw IOException()
//    } else {
//      mFileInputStream = FileInputStream(mFd)
//      mFileOutputStream = FileOutputStream(mFd)
//    }
//  }
//
//  enum class FLOWCON(var flowCon: Int) {
//    NONE(0),
//    HARD(1),
//    SOFT(2)
//
//  }
//
//  enum class PARITY(var parity: Int) {
//    NONE(0),
//    ODD(1),
//    EVEN(2)
//
//  }
//
//  enum class DATAB(var dataBit: Int) {
//    CS5(5),
//    CS6(6),
//    CS7(7),
//    CS8(8)
//
//  }
//
//  enum class STOPB(var stopBit: Int) {
//    B1(1),
//    B2(2)
//
//  }
//
//  enum class BAUDRATE(var baudrate: Int) {
//    B0(0),
//    B50(50),
//    B75(75),
//    B110(110),
//    B134(134),
//    B150(150),
//    B200(200),
//    B300(300),
//    B600(600),
//    B1200(1200),
//    B1800(1800),
//    B2400(2400),
//    B4800(4800),
//    B9600(9600),
//    B19200(19200),
//    B38400(38400),
//    B57600(57600),
//    B115200(115200),
//    B230400(230400),
//    B460800(460800),
//    B500000(500000),
//    B576000(576000),
//    B921600(921600),
//    B1000000(1000000),
//    B1152000(1152000),
//    B1500000(1500000),
//    B2000000(2000000),
//    B2500000(2500000),
//    B3000000(3000000),
//    B3500000(3500000),
//    B4000000(4000000)
//
//  }
//
//  companion object {
//    private const val TAG = "SerialPort"
//    private external fun open(
//      var0: String,
//      var1: Int,
//      var2: Int,
//      var3: Int,
//      var4: Int,
//      var5: Int,
//      var6: Int
//    ): FileDescriptor?
//
//    init {
//      System.loadLibrary("serialport")
//    }
//  }
//}

internal class SerialComAdapterImpl(

):SerialComAdapter{

  override fun setBaudRate(iBaud: Int) {

  }

  override fun setDataBits(dataBits: Int) {
    TODO("Not yet implemented")
  }

  override fun setFlowCon(flowCon: Int) {
    TODO("Not yet implemented")
  }

  override fun setParity(parity: Int) {
    TODO("Not yet implemented")
  }

  override fun setPort(sPort: String) {
    TODO("Not yet implemented")
  }

  override fun setStopBits(stopBits: Int) {
    TODO("Not yet implemented")
  }

  override fun init(){
    
  }
}


