package io.getstream.webrtc.sample.compose.car

import io.getstream.webrtc.sample.compose.car.serialcom.SerialComAdapter

class CarManagerImp(
  private val serialcom: SerialComAdapter
):CarManager{
  private val carOnLine: Boolean=false;
  override fun carBattery() {
    TODO("Not yet implemented")
  }

  override fun carOffCharge() {
    TODO("Not yet implemented")
  }

  override fun carOffline() {
    TODO("Not yet implemented")
  }

  override fun carOnCharge() {
    TODO("Not yet implemented")
  }

  override fun carOnline() {
    TODO("Not yet implemented")
  }

  override fun carReady() {
    TODO("Not yet implemented")
  }

  override fun carSignal() {
    TODO("Not yet implemented")
  }

  override fun move() {

  }


}