/*
 * Copyright 2023 Stream.IO, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.webrtc.sample.compose

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.getstream.webrtc.sample.compose.car.Camera.CameraManagerImp
import io.getstream.webrtc.sample.compose.car.Camera.CameraViewModel
import io.getstream.webrtc.sample.compose.car.Camera.CameraViewScreen
import io.getstream.webrtc.sample.compose.car.CarManager
import io.getstream.webrtc.sample.compose.car.CarManagerImp
import io.getstream.webrtc.sample.compose.car.CarScreen
import io.getstream.webrtc.sample.compose.car.JoyStick.JoyStickScreen
import io.getstream.webrtc.sample.compose.car.JoyStick.MyJoyStickViewModel
import io.getstream.webrtc.sample.compose.car.StateBar.CarStateBarViewModel
import io.getstream.webrtc.sample.compose.car.StateBar.StateBarScreen
import io.getstream.webrtc.sample.compose.car.carScreenViewModel
import io.getstream.webrtc.sample.compose.car.serialcom.SerialComManager
import io.getstream.webrtc.sample.compose.car.serialcom.SerialComManagerImp
import io.getstream.webrtc.sample.compose.car.serialcom.SerialComScreen
import io.getstream.webrtc.sample.compose.car.serialcom.SerialComViewModel
import io.getstream.webrtc.sample.compose.ui.screens.stage.StageScreen
import io.getstream.webrtc.sample.compose.ui.screens.video.VideoCallScreen
import io.getstream.webrtc.sample.compose.ui.theme.WebrtcSampleComposeTheme
import io.getstream.webrtc.sample.compose.webrtc.SignalingClient
import io.getstream.webrtc.sample.compose.webrtc.peer.StreamPeerConnectionFactory
import io.getstream.webrtc.sample.compose.webrtc.sessions.LocalWebRtcSessionManager
import io.getstream.webrtc.sample.compose.webrtc.sessions.WebRtcSessionManager
import io.getstream.webrtc.sample.compose.webrtc.sessions.WebRtcSessionManagerImpl

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), 0)

    val sessionManager: WebRtcSessionManager = WebRtcSessionManagerImpl(
      context = this,
      signalingClient = SignalingClient(),
      peerConnectionFactory = StreamPeerConnectionFactory(this)
    )

    //sessionManager.signalingClient.myws.send("hhhhhhhhhhhhhhhhhhhhhhjjjjjjjjjj"); //just test
    val carmanager:CarManager=CarManagerImp(
      context =this,
      serialCom = SerialComManagerImp(this),
      carCamera = CameraManagerImp(this)
    )


    val serialComManager:SerialComManager=SerialComManagerImp(context=this)

    setContent {
      WebrtcSampleComposeTheme {
        CompositionLocalProvider(LocalWebRtcSessionManager provides sessionManager) {
          // A surface container using the 'background' color from the theme
          Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
          ) {
            var onCallScreen by remember { mutableStateOf(false) }
            var onOpenSerialCom by remember{ mutableStateOf(false) }
            var onJoyStickScreen by remember{ mutableStateOf(false) }
            var onStateBarScreen by remember{mutableStateOf(false) }
            var onCameraViewScreen by remember{ mutableStateOf(false) }
            var onCarScreen by remember{ mutableStateOf(false) }
            val state by sessionManager.signalingClient.sessionStateFlow.collectAsState()



            if (!onCallScreen) {
              if(!onOpenSerialCom){
                StageScreen(state = state,{onOpenSerialCom=true}) { onCallScreen = true }
              }else{
                if(onJoyStickScreen) {
                  JoyStickScreen(MyJoyStickViewModel(serialComManager))
                }
                else if(onStateBarScreen){
                  StateBarScreen(CarStateBarViewModel(serialComManager))

                }else if(onCameraViewScreen){
                  CameraViewScreen(CameraViewModel(carmanager.carCamera))
                }else if(onCarScreen){
                  CarScreen(carScreenViewModel(carmanager))
                }else {
                  SerialComScreen(
                    SerialComViewModel(serialComManager),
                    { onJoyStickScreen = true },
                    { onStateBarScreen = true },
                    { onCameraViewScreen=true},
                    {onCarScreen=true})

                }
              }
            }
            else {

              VideoCallScreen()
            }
          }
        }
      }
    }
  }
}
