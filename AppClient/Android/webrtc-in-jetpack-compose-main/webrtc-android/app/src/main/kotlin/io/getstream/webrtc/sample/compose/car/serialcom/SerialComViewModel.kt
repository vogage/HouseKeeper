package io.getstream.webrtc.sample.compose.car.serialcom

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


sealed interface SerialUiState{
  val isSelectedDeviceItem:Boolean
  val errorMessage:List<ErrorMessage>
  val msg:String
  data class NoDeviceItem(
    override val isSelectedDeviceItem: Boolean=false,
    override val errorMessage: List<ErrorMessage> = emptyList(),
    override val msg:String="",

  ):SerialUiState

  data class HasDeviceItem(
    override val isSelectedDeviceItem: Boolean=true,
    override val errorMessage: List<ErrorMessage> = emptyList(),
    val deviceList:List<DeviceItem> = emptyList(),
    override val msg:String="",
  ):SerialUiState
}
data class ErrorMessage(val id:Long,@StringRes val messageId:Int)
class SerialComViewModel(
  private val serialComManager: SerialComManager
  ):ViewModel() {

  private val _serialViewModelStateFlow = MutableStateFlow(SerialViewModelState())

  val uiStateFlow= _serialViewModelStateFlow// expose UI to serialComScreen
    .map{it.toUiState()}
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = _serialViewModelStateFlow.value.toUiState() // provide a initial value
    )




  fun test(){
    serialComManager.initial()
  }
  init{
    refresh()

  }
  fun updateDriverList(){
    serialComManager.updateUsbAvailableDrivers()
  }
  private fun refresh():Unit{

  }

}




data class SerialViewModelState(
   val msg:String="",
   var selectedId:Int?=-1,
   var devicesList:MutableList<DeviceItem>?=mutableListOf()
) {
  fun toUiState(): SerialUiState =
    if (devicesList == null || devicesList?.isEmpty() == true) {
      SerialUiState.NoDeviceItem(
      )
    } else {
      SerialUiState.HasDeviceItem(
        deviceList = devicesList!!
      )
    }
  }


