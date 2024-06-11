package io.getstream.webrtc.sample.compose.car.serialcom

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel


data class SerialUiState(
  val isSelectedDeviceItem:Boolean=false,
  val errorMessage:List<ErrorMessage> = emptyList(),
  val msg:String="",
  val deviceList:List<DeviceItem> = emptyList()
)
data class ErrorMessage(val id:Long,@StringRes val messageId:Int)
class SerialComViewModel(
    private val SerialCom: SerialComLayer
  ):ViewModel() {

  fun test(){

  }

  fun updateDriverList(){

  }
  private fun refresh():Unit{

  }

}



