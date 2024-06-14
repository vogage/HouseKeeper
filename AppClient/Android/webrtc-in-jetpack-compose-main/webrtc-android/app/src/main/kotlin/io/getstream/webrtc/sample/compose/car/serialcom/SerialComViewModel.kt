package io.getstream.webrtc.sample.compose.car.serialcom

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class SerialUiState(
  val errorMessage:List<ErrorMessage> = emptyList(),
  val msg:String="",
  var deviceList:List<DeviceItem> = emptyList()
)
data class ErrorMessage(val id:Long,@StringRes val messageId:Int)


class SerialComViewModel(
  private val serialCom: SerialComManager
  ):ViewModel() {
  private var _uiState = MutableStateFlow(SerialUiState())
  val uiState: StateFlow<SerialUiState> = _uiState

  init {
    observeSerialComState()
  }
  private fun observeSerialComState(){
    viewModelScope.launch {
      serialCom.serialState.collect{ it ->
        when(it){
          SerialComState.Connected -> {
            _uiState.update {
              ui -> ui.copy(msg = " the serial port is connected!!!")
            }
          }
          SerialComState.UsbDetached -> {
            _uiState.update {
              ui -> ui.copy(deviceList = serialCom.getAvailableDevices(),
                  msg="UsbDetached!!!")
            }
          }
          SerialComState.UsbAttached ->{
            _uiState.update {
              ui -> ui.copy(deviceList = serialCom.getAvailableDevices(),
                  msg="UsbAttached!!!")}
          }
          SerialComState.Active -> {}
          SerialComState.Disable -> {
            _uiState.update { ui -> ui.copy(msg = "Disable!!!") }
          }
          SerialComState.Creating -> {}
          SerialComState.Closed ->{}
          SerialComState.Working -> {}
          SerialComState.Error -> {
            _uiState.update { ui -> ui.copy(msg = "Error!!! \n"+serialCom.getErrorMsg()) }
          }
        }
      }
    }
  }



  fun test(){

  }

  fun connect(){ serialCom.connect() }

}



