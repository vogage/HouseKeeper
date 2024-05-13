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

package io.getstream.webrtc.sample.compose.ui.screens.stage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.webrtc.sample.compose.R
import io.getstream.webrtc.sample.compose.webrtc.WebRTCSessionState

@Composable
fun StageScreen(
  state: WebRTCSessionState,
  onOpenSerialCom: () -> Unit,
  onJoinCall: () -> Unit
) {
  Box(modifier = Modifier.fillMaxSize()) {
    var enabledCall by remember { mutableStateOf(false) }

    val text = when (state) {
      WebRTCSessionState.Offline -> {
        enabledCall = false
        stringResource(id = R.string.button_start_session)
      }
      WebRTCSessionState.Impossible -> {
        enabledCall = false
        stringResource(id = R.string.session_impossible)
      }
      WebRTCSessionState.Ready -> {
        enabledCall = true
        stringResource(id = R.string.session_ready)
      }
      WebRTCSessionState.Creating -> {
        enabledCall = true
        stringResource(id = R.string.session_creating)
      }
      WebRTCSessionState.Active -> {
        enabledCall = false
        stringResource(id = R.string.session_active)
      }
    }

    Column(
      modifier = Modifier
        .fillMaxWidth() // 填充父布局的宽度
        .padding(16.dp) // 添加外部间距
    ) {
      Button(
        enabled = enabledCall,
        onClick = { onJoinCall.invoke() }
      ) {
        Text(
          text = text,
          fontSize = 26.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Button(
        enabled = true,
        onClick = { onOpenSerialCom.invoke() }
      ) {
        Text(
          text = "SerialPort",
          fontSize = 26.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
