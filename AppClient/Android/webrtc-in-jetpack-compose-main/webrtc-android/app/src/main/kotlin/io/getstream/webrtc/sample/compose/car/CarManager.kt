package io.getstream.webrtc.sample.compose.car

interface CarManager {

  fun carReady()

  fun carOnline()

  fun carOffline()

  fun carOnCharge()

  fun carOffCharge()

  fun move()

  fun carBattery()

  fun carSignal()


}