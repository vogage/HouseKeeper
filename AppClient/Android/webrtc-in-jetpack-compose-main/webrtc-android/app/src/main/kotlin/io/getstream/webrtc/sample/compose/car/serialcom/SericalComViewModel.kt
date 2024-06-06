package io.getstream.webrtc.sample.compose.car.serialcom

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


sealed interface SerialUiState{
  val isLoading:Boolean
  val errorMessage:List<ErrorMessage>
  data class NoDeviceItem(
    override val isLoading: Boolean,
    override val errorMessage: List<ErrorMessage>
  ):SerialUiState

  data class HasDeviceItem(
    override val isLoading: Boolean,
    override val errorMessage: List<ErrorMessage>
  ):SerialUiState
}
data class ErrorMessage(val id:Long,@StringRes val messageId:Int)
class SericalComViewModel(
  private val serialComManager:SerialComManager
):ViewModel() {



  private val _serialviewmodelstateFlow = MutableStateFlow(CreateSerialViewModelSate(serialComManager))
  val serialviewmodelstateFlow=_serialviewmodelstateFlow.asStateFlow()

  private fun CreateSerialViewModelSate(serialcommanager: SerialComManager):SerialViewModelState{
    return SerialViewModelState(
      t="fahfhhhh"
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
   fun toUiState():SerialUiState=
     if(devicesList==null|| devicesList?.isEmpty() == true){
       SerialUiState.NoDeviceItem(
          isLoading = true,
          errorMessage = emptyList()
       )
     }else{
       SerialUiState.HasDeviceItem(
         isLoading = false,
         errorMessage = emptyList()
       )
     }
}
