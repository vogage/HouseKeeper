package io.getstream.webrtc.sample.compose.car.serialcom

enum class FrameFlag(val flag: UByte){
  Waiting(11u),
  Running(0u);
}
data class CommunicationMessage(// default test message
  var Head:UByte=100u,
  var Flag: FrameFlag = FrameFlag.Waiting,
  var SeqNum:UByte=1u,
  var FBSpeed:UByte=0u,
  var LRSpeed:UByte=0u,
  var ActiveCarCameraPitch:Boolean=false,
  var CarCameraPitch:Boolean=false,// true in the positive direction,false in the negative direction
  var CheckSum:UByte
){
  fun toByteArray(){
    return this.toByteArray()
  }
}