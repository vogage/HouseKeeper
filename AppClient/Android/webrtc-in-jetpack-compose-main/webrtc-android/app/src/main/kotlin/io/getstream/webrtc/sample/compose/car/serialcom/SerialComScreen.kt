package io.getstream.webrtc.sample.compose.car.serialcom

import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SerialComScreen(

){
   Column (
     modifier = Modifier
       .padding(vertical = 10.dp)
   ){
      DeiveItem()
   }
}

@Composable
fun DeiveItem(){
  Text(
    text="hhhhhhh"
  )
}