package io.getstream.webrtc.sample.compose.car.serialcom

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


sealed interface SerialUiState{
  val isLoading:Boolean
  val errorMessage:List<ErrorMessage>
  val hhtt:String
  data class NoDeviceItem(
    override val isLoading: Boolean=true,
    override val errorMessage: List<ErrorMessage> = emptyList(),
    override val hhtt:String="hhhhhhhhhhhhhhbbbbbb"
  ):SerialUiState

  data class HasDeviceItem(
    override val isLoading: Boolean,
    override val errorMessage: List<ErrorMessage>,
    val deviceList:List<DeviceItem>,
    override val hhtt:String="hhhhhhhhhaaaaaaaaaaaa"
  ):SerialUiState
}
data class ErrorMessage(val id:Long,@StringRes val messageId:Int)
class SericalComViewModel(
  private val serialComManager: SerialComManager
  ):ViewModel() {



  private val _serialViewModelStateFlow = MutableStateFlow(CreateSerialViewModelSate(serialComManager))
  val serialViewModelStateFlow=_serialViewModelStateFlow.asStateFlow()

  val uiStateFlow= _serialViewModelStateFlow
    .map{it.toUiState()}
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = SerialUiState.NoDeviceItem() // 提供一个初始的 UI 状态值
    )

  private fun CreateSerialViewModelSate(serialComManager: SerialComManager):SerialViewModelState{
    return SerialViewModelState(
      t= "fahfhhhh202406072049"
    )
  }

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
   val t:String?=null,
   var devicesList:MutableList<DeviceItem>?=mutableListOf()
){
   fun toUiState():SerialUiState =
    if (devicesList == null || devicesList?.isEmpty() == true) {
       SerialUiState.NoDeviceItem(
         isLoading = true,
         errorMessage = emptyList()
       )
     } else {
       SerialUiState.HasDeviceItem(
         isLoading = false,
         errorMessage = emptyList(),
         deviceList = devicesList!!,
         hhtt = t!!
       )
     }
   }

