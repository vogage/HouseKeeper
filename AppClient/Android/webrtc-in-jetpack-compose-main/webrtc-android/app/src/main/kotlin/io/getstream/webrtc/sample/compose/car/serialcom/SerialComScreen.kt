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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.getstream.webrtc.sample.compose.ui.theme.Teal200

@Composable
fun SerialComScreen(
   viewModel: SericalComViewModel
){
   val uiState by viewModel.uiStateFlow.collectAsState()

  Column (
     modifier = Modifier
       .padding(vertical = 10.dp)

   ){
      when(uiState){
        is SerialUiState.HasDeviceItem ->
          HasDeviceItemContent(uiState as SerialUiState.HasDeviceItem)
        is SerialUiState.NoDeviceItem ->
          NoDeviceItemContent(uiState as SerialUiState.NoDeviceItem)
      }
   }
}
@Composable
private fun HasDeviceItemContent(
  uiState: SerialUiState.HasDeviceItem
){
  Column() {
    uiState.deviceList.forEach { device ->
      DeviceItemCard(
        baud = device.bauRate

      )
    }
  }
}
@Composable
private fun NoDeviceItemContent(
  uiState: SerialUiState.NoDeviceItem
){

}
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DeviceItemCard(
  //toggleSelection:()->Unit,
  modifier:Modifier=Modifier,
  isSelected:Boolean=false,
  baud:Int=-1,
  id:Int=-1
): Unit {
  Card(
    modifier = modifier
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .semantics { selected = isSelected },
    backgroundColor = Teal200,
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
        Text(baud.toString())
      }
    }

  }
}