package io.getstream.webrtc.sample.compose.car.JoyStick

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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



    Column (
      modifier = Modifier.padding(10.dp)
    ){
      val uiState by joyStickViewModel.uiState.collectAsState()
      Spacer(modifier = Modifier.height(300.dp))

      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          MyJoyStick(joyStickViewModel)
          MyJoyStick2(joyStickViewModel)
        }

      }
      Spacer(modifier = Modifier.height(30.dp))
      Text(
        text="IsDragging :"+uiState.isDragging
      )
      Spacer(modifier = Modifier.height(30.dp))
      Text(
        text="Position :"+uiState.currentPosition
      )
      Spacer(modifier = Modifier.height(30.dp))
      Text(

        text="x: "+uiState.x+",y: "+uiState.y
      )
    }



}