package io.getstream.webrtc.sample.compose.car.JoyStick

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import io.getstream.log.taggedLogger
import io.getstream.webrtc.sample.compose.car.serialcom.SerialComManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

data class MyJoyStickUiState(
  val msg : String= "here is msg",
  var x:Int=0,
  var y:Int=0,
  var isDragging:Boolean=false,
  var currentPosition:Position=Position.Top,
  val buttonSizePx : Float =0f
){

}
class MyJoyStickViewModel(
  private val serialCom: SerialComManager,
): ViewModel() {
  private val logger by taggedLogger("Call:MyJoyStickViewModel")

  private var _uiState = MutableStateFlow(MyJoyStickUiState())
  val uiState: StateFlow<MyJoyStickUiState> = _uiState

  suspend fun SendJoyStickPosition(){
    withContext(Dispatchers.IO){
      serialCom.send("fdafadasfad")
    }
  }

  fun refreshOffset(newOffsetX:Float,newOffsetY:Float){
    val newPosition = getPosition(
      offset = Offset(newOffsetX, newOffsetY),
      buttonSizePx = uiState.value.buttonSizePx
    )

    if(newPosition!=null) {
      _uiState.update {
        MyJoyStickUiState(currentPosition = newPosition, isDragging = true)
      }
    }
  }

}