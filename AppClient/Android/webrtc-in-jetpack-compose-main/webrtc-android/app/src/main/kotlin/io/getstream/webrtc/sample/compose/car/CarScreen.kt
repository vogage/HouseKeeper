package io.getstream.webrtc.sample.compose.car

import android.graphics.SurfaceTexture
import android.view.TextureView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.getstream.webrtc.sample.compose.car.JoyStick.MyJoyStick2
import io.getstream.webrtc.sample.compose.car.StateBar.CarStatusBar

@Composable
fun CarScreen(
  viewModel: carScreenViewModel
) {
  Box(
  contentAlignment = Alignment.Center
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        val uiBarState by viewModel.uiStateBar.collectAsState()
        CarStatusBar(uiState = uiBarState)
      }

        Box() {
          CameraView2(
            viewModel = viewModel
          ) {}
          MyJoyStick2(viewModel.myJoyStickViewModel)
        }

    }
  }
}



@Composable
fun CameraView2(
  viewModel: carScreenViewModel,
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

