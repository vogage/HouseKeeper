package io.getstream.webrtc.sample.compose.car

import android.graphics.SurfaceTexture
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.log.taggedLogger
import io.getstream.webrtc.sample.compose.car.Camera.CameraUIState
import io.getstream.webrtc.sample.compose.car.JoyStick.MyJoyStickUiState
import io.getstream.webrtc.sample.compose.car.JoyStick.getPosition
import io.getstream.webrtc.sample.compose.car.StateBar.StateBarData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class carScreenViewModel(
  private val carManager: CarManager
):ViewModel(){
  private val logger by taggedLogger("Call:MyJoyStickViewModel")
  private var _uiStateBar = MutableStateFlow(StateBarData())
  val uiStateBar: StateFlow<StateBarData> = _uiStateBar


  private var _uiStateJoyStick = MutableStateFlow(MyJoyStickUiState())
  val uiStateJoyStick: StateFlow<MyJoyStickUiState> = _uiStateJoyStick

  private val _cameraUiState = MutableStateFlow(CameraUIState())
  val cameraUiState: StateFlow<CameraUIState> = _cameraUiState
  private val scope=viewModelScope

  fun changeVolume(vol:Float){
    _uiStateBar.update { it-> it.copy(batteryVolume = vol)}
  }

  fun changeSignalStrength(stren:Float){
    _uiStateBar.update { it-> it.copy(signalStrength = stren) }
  }



  suspend fun SendJoyStickPosition(){
    withContext(Dispatchers.IO){
      carManager.serialCom.send("fdafadasfad")
    }
  }

  fun refreshPosition(newOffsetX:Float,newOffsetY:Float){
    val newPosition = getPosition(
      offset = Offset(newOffsetX, newOffsetY),
      buttonSizePx = _uiStateJoyStick.value.buttonSizePx
    )
    if (newPosition != null) {
      _uiStateJoyStick.update {
        MyJoyStickUiState(currentPosition = newPosition)
      }
    }
  }
  fun refreshIsDragging(flag:Boolean){
    _uiStateJoyStick.update { MyJoyStickUiState(isDragging = flag) }
  }
  fun refreshOffset(newOffsetX:Float,newOffsetY:Float){
    _uiStateJoyStick.update {
      MyJoyStickUiState(
        x=newOffsetX,
        y=newOffsetY,
        isDragging = true
      )
    }
  }



  fun openCamera(p0: SurfaceTexture, width: Int, height: Int, onError:(Exception)->Unit){
    carManager.carCamera.openCamera(p0,width,height,onError)
  }

  fun closeCamera(onError:(Exception)->Unit){
    carManager.carCamera.closeCamera(onError)
  }

  fun setMsg(msg:String){
    _cameraUiState.update { it.copy(stateMsg =  msg) }
  }
  init {
    scope.launch {
      refreshCameraUiSate()
    }

  }
  private suspend fun refreshCameraUiSate(){
    carManager.carCamera.cameraState.collect{
        state ->
      _cameraUiState.update { it.copy(stateMsg = state.msg) }
    }
  }

  fun initial(){
    carManager.carCamera.initial()
  }


}