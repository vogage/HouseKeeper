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
  var sendStr:String="",
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
          SerialComState.Received -> {
            _uiState.update {
              ui -> ui.copy(msg = serialCom.getReceivedMsg())
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
          SerialComState.Selected -> {}
          SerialComState.Disable -> {
            _uiState.update { ui -> ui.copy(msg = "Disable!!!") }
          }
          SerialComState.Initialized -> {
            _uiState.update { ui->ui.copy(msg="Initialized!!!!") }
          }
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
  fun clearMsg(){
    serialCom.clearMsg()
    _uiState.update {
    it.copy(msg="")
  }}



  fun refresh(){
    try {
      serialCom.updateUsbAvailableDrivers()
      val deivces: List<DeviceItem> = serialCom.getAvailableDevices()
      _uiState.update { it.copy(deviceList = deivces, msg = serialCom.getErrorMsg()) }

    }catch(e :Exception){
      _uiState.update {  it.copy(msg =serialCom.getErrorMsg()+e.toString())  }
    }


  }
  fun connect(){ serialCom.connect() }
  fun initial(){serialCom.initial()}
  fun updateSendStr(str:String){
    _uiState.update { it.copy(sendStr = str) }
  }
  fun getMsg(){
    _uiState.update { it.copy(msg=serialCom.getErrorMsg()) }
  }
  fun sendMsg(){
    serialCom.send(uiState.value.sendStr)
    _uiState.update { it.copy(msg =serialCom.getErrorMsg()) }
  }

}



