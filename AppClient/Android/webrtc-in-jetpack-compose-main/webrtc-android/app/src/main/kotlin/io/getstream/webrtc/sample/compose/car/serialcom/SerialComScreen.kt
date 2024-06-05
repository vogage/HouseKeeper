package io.getstream.webrtc.sample.compose.car.serialcom


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SerialComScreen(
   viewModel: SericalComViewModel
){
   Column (
     modifier = Modifier
       .padding(vertical = 10.dp)
   ){
      DeviceItemCard { viewModel.test() }
   }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeviceItemCard(
  onClick:()->Unit

){
  Card(
    onClick = onClick
  ) {
    Text( "hhhhhhh")
  }
}