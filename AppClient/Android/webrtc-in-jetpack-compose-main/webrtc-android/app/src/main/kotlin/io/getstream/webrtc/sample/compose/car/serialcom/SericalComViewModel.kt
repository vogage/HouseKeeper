package io.getstream.webrtc.sample.compose.car.serialcom

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SericalComViewModel(private val serialcommanager:SerialComManager
):ViewModel() {



  private val _serialviewmodelstateFlow = MutableStateFlow(
    CreateSerialViewModelSate(serialcommanager)
  )
  val serialviewmodelstateFlow: StateFlow<SerialViewModelState>get()= _serialviewmodelstateFlow

    private fun CreateSerialViewModelSate(serialcommanager: SerialComManager):SerialViewModelState{
      return SerialViewModelState(
        t="fahfhhhh"
      )
    }

  fun init():Unit{
    refresh()
  }

  private fun refresh():Unit{

  }
}

data class SerialViewModelState(
   val t:String?=null
){

}
