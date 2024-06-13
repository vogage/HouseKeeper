package io.getstream.webrtc.sample.compose.car.serialcom


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.getstream.webrtc.sample.compose.ui.theme.Purple500
import io.getstream.webrtc.sample.compose.ui.theme.Teal200

@Composable
fun SerialComScreen(
   viewModel: SerialComViewModel
){
  val uiState=viewModel.uiState.collectAsState()


  Column (
     modifier = Modifier
       .padding(vertical = 10.dp)

   ){
      uiState.value.deviceList

   }
}
@Composable
private fun HasDeviceItemContent(

){
  Column() {


  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DeviceItemCard(
  toggleSelection:()->Unit,
  modifier:Modifier=Modifier,
  isSelected:Boolean=false,
  id:Int,
  remark:String
): Unit {
  Card(
    modifier = modifier
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .semantics { selected = isSelected },
    backgroundColor = if(isSelected)Teal200 else Purple500,
   //onClick = toggleSelection
  ) {
    Column(
       modifier = Modifier
         .fillMaxWidth()
         .padding(20.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
      ){
        Text("hfaklsdfhlkfjlakfjla" )
        //Text(uiState.devicesList?.get(0)?.bauRate.toString())
      }
      Row(
        modifier = Modifier
          .fillMaxWidth()
      ){
        Text(remark)
      }
    }

  }
}