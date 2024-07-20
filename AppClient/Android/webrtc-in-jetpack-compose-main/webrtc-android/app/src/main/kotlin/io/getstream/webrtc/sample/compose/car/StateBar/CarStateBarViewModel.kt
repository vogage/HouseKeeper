package io.getstream.webrtc.sample.compose.car.StateBar

import androidx.lifecycle.ViewModel
import io.getstream.webrtc.sample.compose.car.serialcom.SerialComManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class StateBarData(
  var batteryVolume:Float=0f,
  var signalStrength:Float=0f
)
class CarStateBarViewModel(
  private val serialCom: SerialComManager
):ViewModel(){
  private var _uiState = MutableStateFlow(StateBarData())
  val uiState: StateFlow<StateBarData> = _uiState


  fun changeVolume(vol:Float){
    _uiState.update { it-> it.copy(batteryVolume = vol)}
  }

  fun changeSignalStrength(stren:Float){
    _uiState.update { it-> it.copy(signalStrength = stren) }
  }
}


