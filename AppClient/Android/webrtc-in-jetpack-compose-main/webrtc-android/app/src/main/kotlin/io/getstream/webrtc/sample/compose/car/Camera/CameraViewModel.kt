package io.getstream.webrtc.sample.compose.car.Camera



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CameraUIState(
  var stateMsg:String =""
)
class CameraViewModel(
  private val carCamera:CameraManger
): ViewModel() {

  private val _cameraUiState = MutableStateFlow(CameraUIState())
  val cameraUiState: StateFlow<CameraUIState> = _cameraUiState
  private val scope=viewModelScope
init {
  scope.launch {
    refreshCameraUiSate()
  }

}
  private suspend fun refreshCameraUiSate(){
    carCamera.cameraState.collect{
      state ->
      _cameraUiState.update { it.copy(stateMsg = state.msg) }
    }
  }

  fun initial(){
    carCamera.initial()
  }
}

