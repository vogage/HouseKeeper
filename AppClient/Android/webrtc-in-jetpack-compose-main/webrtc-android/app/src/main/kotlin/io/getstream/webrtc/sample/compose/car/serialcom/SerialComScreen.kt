package io.getstream.webrtc.sample.compose.car.serialcom




import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.getstream.webrtc.sample.compose.ui.theme.HaHa
import io.getstream.webrtc.sample.compose.ui.theme.Purple500
import io.getstream.webrtc.sample.compose.ui.theme.Teal200

@Composable
fun SerialComScreen(
   viewModel: SerialComViewModel
){
  val uiState by viewModel.uiState.collectAsState()

  Column {
    Text(
      text = uiState.msg,
      modifier = Modifier
        .padding(vertical = 20.dp)
    )
    Button(onClick = {viewModel.initial()}) {
      Text(text = "initial")
    }
    Button(onClick = {viewModel.refresh()}) {
      Text(text = "refresh")
    }
    Button(onClick = {viewModel.getMsg()}) {
      Text(text = "getMsg")
    }
    Button(onClick = { viewModel.clearMsg() }) {
      Text(text=" clearMsg ")
    }
      Button(onClick = { viewModel.requestPermission() }) {
      Text(text="requestPermission")
    }
    Button(onClick = { viewModel.connect() }) {
      Text(text=" Connect ")
    }
    Button(onClick = {viewModel.testSerial()}) {
      Text(text = "testserial")
    }
    Column (
      modifier = Modifier
        .padding(vertical = 10.dp)
    ){
      HasDeviceItemContent(devices = uiState.deviceList,
        toggleSelection = { /*TODO*/ },
        onClickAction ={viewModel.connect() }
      )
    }
    PaddingValues(vertical = 50.dp)
    SerialComSendTextField(
      text = uiState.sendStr,
      onValueChanged = {str -> viewModel.updateSendStr(str) }
    )
    Button(onClick = {viewModel.sendMsg()}) {
      Text(text = "send")
    }
  }


}

@Composable
private fun SerialComSendTextField(
  text: String,
  onValueChanged : (String)->Unit,
  modifier: Modifier=Modifier
){
  OutlinedTextField(
    value = text,
    onValueChange = {newText ->onValueChanged(newText)},
    label={Text("input")},
    modifier= modifier
      .padding(20.dp)
      .fillMaxWidth()
      .height(80.dp)
  )
}

@Composable
private fun HasDeviceItemContent(
  devices: List<DeviceItem>,
  toggleSelection: () -> Unit,
  onClickAction:() -> Unit
){
  Column() {
    devices.forEach{
      DeviceItemCard(toggleSelection = {}, onClickAction = onClickAction, item = it , remark = "hhhhh" )
    }

  }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun DeviceItemCard(
  toggleSelection: ()->Unit,
  onClickAction:() -> Unit,
  modifier:Modifier=Modifier,
  isSelected:Boolean=false,
  item: DeviceItem,
  remark:String
): Unit {
  Card(
    modifier = modifier
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .semantics { selected = isSelected }
      .combinedClickable(
        onClick = toggleSelection,
        onDoubleClick = onClickAction
      ),
    backgroundColor = if(isSelected)Teal200 else Purple500,
   //onClick = toggleSelection
  ) {
    Column(
       modifier = Modifier
         .fillMaxWidth()
         .padding(20.dp)
    ) {
      ItemSetsText("BauRate:"+item.bauRate)
      ItemSetsText("dataBits:"+item.dataBits)
      ItemSetsText("stopBits:"+item.stopBits)
      ItemSetsText("idOfItem:"+item.idOfItem)
      ItemSetsText("productId:"+item.productId)
      FloatingActionButton(
        onClick = onClickAction,

        modifier= Modifier
          .padding(20.dp)
          .background(HaHa)

      ){
        Text("CONNECT")
      }
    }

  }
}
@Composable
fun ItemSetsText(text:String){
  Row(
    modifier = Modifier
      .fillMaxWidth()
  ){
    Text(text)
  }
}

@Preview
@Composable
fun DeviceItemCardPreview(){
  DeviceItemCard(toggleSelection = { /*TODO*/ }, item = DeviceItem(), onClickAction= {},remark = "")
}