package io.getstream.webrtc.sample.compose.car.serialcom

interface SerialComAdapter {

  fun init()

  fun open()

  fun connect()

  fun send()

  fun read()

  fun close()
}