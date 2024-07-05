package io.getstream.webrtc.sample.compose.car.JoyStick

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun JoyStickScreen(
  joyStickViewModel: MyJoyStickViewModel
){


    Box(
      contentAlignment = Alignment.Center) {
      Column {
        val uiState by joyStickViewModel.uiState.collectAsState()
        MyJoyStick(joyStickViewModel)
        Spacer(modifier = Modifier.height(30.dp))
        Text(
          text="IsDragging :"+uiState.isDragging
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
          text="Positon :"+uiState.currentPosition
        )
      }

    }

}