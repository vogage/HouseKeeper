package io.getstream.webrtc.sample.compose.car.Camera

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import androidx.core.content.getSystemService
import io.getstream.log.taggedLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


class CameraManagerImp(private val context: Context):CameraManger{
  private val cameraManager by lazy { context.getSystemService<CameraManager>() }
  private val logger by taggedLogger("Call:CameraManagerImp")
  private var _cameraState = MutableStateFlow(CameraState())
  override val cameraState: StateFlow<CameraState> =_cameraState
  override fun initial() {
    val manager = cameraManager ?: throw RuntimeException("CameraManager was not initialized!")

    val ids = manager.cameraIdList
    var foundCamera = false
    var cameraId = ""

    for (id in ids) {
      val characteristics = manager.getCameraCharacteristics(id)
      val cameraLensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)

      if (cameraLensFacing == CameraMetadata.LENS_FACING_FRONT) {
        foundCamera = true
        cameraId = id
      }
    }

    if (!foundCamera && ids.isNotEmpty()) {
      cameraId = ids.first()
    }

    _cameraState.update { it.copy(msg = "get the CameraId$cameraId") }
  }


}


