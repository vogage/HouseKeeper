package io.getstream.webrtc.sample.compose.car.Camera

import android.graphics.SurfaceTexture
import android.view.TextureView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraViewScreen(
  viewModel: CameraViewModel
){
  val uiState by viewModel.cameraUiState.collectAsState()

  Column {

    Text(text = uiState.stateMsg)
    Button(
      onClick = {viewModel.initial()}
    ){
      Text("initial Camera")
    }
    Text(text = "cameraView")
    CameraView(viewModel){}
    Text(text = "cameraViewButton")

  }
}


@Composable
fun CameraView(
  viewModel: CameraViewModel,
  onError:(Exception)->Unit
){
  Box(modifier = Modifier.fillMaxSize()
    .border(width = 1.dp, color = Color.Black)
    .padding(5.dp),){
    AndroidView(
      modifier = Modifier.fillMaxSize()
        .border(width = 1.dp, color = Color.Black),
      factory = {
        TextureView(it).apply {
          surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, width: Int, height: Int) {
              // 在这里你可以安全地访问cameraManager并尝试打开相机
              // 注意：这应该在后台线程中完成，以避免阻塞UI线程
              viewModel.openCamera(p0, width, height, onError)
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, width: Int, height: Int) {
              // 可以在这里处理TextureView大小变化的情况
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
              // 清理相机资源
              viewModel.closeCamera(onError)
              return true // 表示已成功处理
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
              // 这不是必须的，除非你需要对每次更新做出响应
            }
          }
        }
      }
    )
  }
}


