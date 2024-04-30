package io.getstream.webrtc.sample.compose.car.serialcom

interface SerialComAdapter {
  fun setPort(sPort: String)

  fun setBaudRate(iBaud: Int)

  fun setStopBits(stopBits: Int)

  fun setDataBits(dataBits: Int)

  fun setParity(parity: Int)

  fun setFlowCon(flowCon: Int)

  fun init()

  fun open()

  fun connect()

  fun send()

  fun read()

  fun close()
}