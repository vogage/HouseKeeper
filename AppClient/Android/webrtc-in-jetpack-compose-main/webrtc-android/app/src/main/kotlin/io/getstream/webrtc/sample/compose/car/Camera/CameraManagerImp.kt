package io.getstream.webrtc.sample.compose.car.Camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.TextureView
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import io.getstream.log.taggedLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


class CameraManagerImp(
  private val context: Context
):CameraManger{
  private val logger by taggedLogger("Call:CameraManagerImp")
  private val cameraManager by lazy { context.getSystemService<CameraManager>() }
  private lateinit var cameraId:String


  private lateinit var backgroundHandlerThread: HandlerThread
  private lateinit var backgroundHandler: Handler
  //private lateinit var textureView: AutoFitTextureView

  //val texture = textureView.surfaceTexture

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

      if (cameraLensFacing == CameraMetadata.LENS_FACING_BACK) {
        foundCamera = true
        cameraId = id
      }
    }

    if (!foundCamera && ids.isNotEmpty()) {
      cameraId = ids.first()
    }

    _cameraState.update { it.copy(msg = "get the CameraId$cameraId") }

    val characteristics: CameraCharacteristics=manager.getCameraCharacteristics(cameraId)

    if (ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      _cameraState.update { it.copy(msg = "checkSelfPermission failed") }
      return
    }
    try {
      startBackgroundThread()
    }catch (e:Exception){
      _cameraState.update { it.copy(msg = "startBackgroundThread:  $e") }
    }
    try {
      manager.openCamera(cameraId,cameraStateCallback,backgroundHandler)
    }catch (e:Exception){
      _cameraState.update { it.copy(msg = "checkSelfPermission:  $e") }
    }
  }


  private fun startBackgroundThread(){
    backgroundHandlerThread= HandlerThread("CameraVideoThread")
    backgroundHandlerThread.start()
    backgroundHandler= Handler(backgroundHandlerThread.looper)
  }

  private fun stopBackgroundThread(){
    backgroundHandlerThread.quitSafely()
    backgroundHandlerThread.join()
  }

  private val cameraStateCallback = object :CameraDevice.StateCallback(){
    override fun onOpened(p0: CameraDevice) {
      _cameraState.update { it.copy(msg = "CameraDevice.StateCallback onOpened") }
    }

    override fun onDisconnected(p0: CameraDevice) {
      TODO("Not yet implemented")
    }

    override fun onError(cameraDevice: CameraDevice, error: Int) {
      when(error) {
        ERROR_CAMERA_DEVICE -> _cameraState.update {it.copy(msg = "ERROR_CAMERA_DEVICE")}
        ERROR_CAMERA_DISABLED -> _cameraState.update {it.copy(msg = "ERROR_CAMERA_DISABLED")}
        ERROR_CAMERA_IN_USE -> _cameraState.update {it.copy(msg = "ERROR_CAMERA_IN_USE")}
        ERROR_CAMERA_SERVICE -> _cameraState.update {it.copy(msg = "ERROR_CAMERA_SERVICE")}
        ERROR_MAX_CAMERAS_IN_USE -> _cameraState.update {it.copy(msg = "ERROR_MAX_CAMERAS_IN_USE")}
        else -> _cameraState.update {it.copy(msg = "CameraDevice Unknown Error")}
      }
    }

  }

  override fun  openCamera(p0: SurfaceTexture, width: Int, height: Int, onError:(Exception)->Unit){

  }

  override fun closeCamera(onError:(Exception)->Unit){

  }
}


class AutoFitTextureView @JvmOverloads constructor(
  context: Context?,
  attrs: AttributeSet? = null,
  defStyle: Int = 0
) :
  TextureView(context!!, attrs, defStyle) {
  private var mRatioWidth = 0
  private var mRatioHeight = 0

  /**
   * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
   * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
   * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
   *
   * @param width  Relative horizontal size
   * @param height Relative vertical size
   */
  fun setAspectRatio(width: Int, height: Int) {
    require(!(width < 0 || height < 0)) { "Size cannot be negative." }
    mRatioWidth = width
    mRatioHeight = height
    requestLayout()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = MeasureSpec.getSize(heightMeasureSpec)
    if (0 == mRatioWidth || 0 == mRatioHeight) {
      setMeasuredDimension(width, height)
    } else {
      if (width < height * mRatioWidth / mRatioHeight) {
        setMeasuredDimension(width, width * mRatioHeight / mRatioWidth)
      } else {
        setMeasuredDimension(height * mRatioWidth / mRatioHeight, height)
      }
    }
  }
}

