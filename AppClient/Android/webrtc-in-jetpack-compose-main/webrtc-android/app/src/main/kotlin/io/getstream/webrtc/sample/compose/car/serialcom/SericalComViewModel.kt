package io.getstream.webrtc.sample.compose.car.serialcom

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


sealed interface SerialUiState{
  val GetDeviceItem:Boolean
  val errorMessage:List<ErrorMessage>
  val hhtt:String
  data class NoDeviceItem(
    override val GetDeviceItem: Boolean=false,
    override val errorMessage: List<ErrorMessage> = emptyList(),
    override val hhtt:String="hhhhhhhhhhhhhhbbbbbb"
  ):SerialUiState

  data class HasDeviceItem(
    override val GetDeviceItem: Boolean=true,
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

  val uiStateFlow= _serialViewModelStateFlow// expose UI to serialComScreen
    .map{it.toUiState()}
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = _serialViewModelStateFlow.value.toUiState() // provide a initial value
    )

  private fun CreateSerialViewModelSate(serialComManager: SerialComManager):SerialViewModelState{
    var vm=SerialViewModelState(
      t= "fahfhhhh202406072049"
    )
    vm.devicesList?.add(DeviceItem(bauRate = 999999))
    vm.devicesList?.add(DeviceItem(bauRate = 888888))
    return vm
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
         GetDeviceItem = false ,
         errorMessage = emptyList()
       )
     } else {
       SerialUiState.HasDeviceItem(
         GetDeviceItem = false,
         errorMessage = emptyList(),
         deviceList = devicesList!!,
         hhtt = t!!
       )
     }
   }

