package io.getstream.webrtc.sample.compose.car.Camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.params.OutputConfiguration
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.util.Size
import android.view.Surface
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
  private lateinit var cameraCharacteristics: CameraCharacteristics

  private lateinit var cameraHandlerThread: HandlerThread
  private lateinit var cameraHandler: Handler
  private lateinit var mCameraDevice: CameraDevice
  private lateinit var mSufaceTexture:SurfaceTexture
  private lateinit var mPreviewSize: Size
  //private lateinit var textureView: AutoFitTextureView

  //val texture = textureView.surfaceTexture

  private var _cameraState = MutableStateFlow(CameraState())
  override val cameraState: StateFlow<CameraState> =_cameraState


  override fun initial() {
    val manager = cameraManager ?: throw RuntimeException("CameraManager was not initialized!")

    val ids = manager.cameraIdList
    var foundCamera = false


    for (id in ids) {
      val characteristics = manager.getCameraCharacteristics(id)
      val cameraLensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)

      if (cameraLensFacing == CameraMetadata.LENS_FACING_BACK) {
        foundCamera = true
        cameraId = id
        cameraCharacteristics=characteristics
        val streamConfigurationMap = cameraCharacteristics.get(
          CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )
      }
    }

    if (!foundCamera && ids.isNotEmpty()) {
      cameraId = ids.first()
    }

    _cameraState.update { it.copy(msg = "get the CameraId$cameraId") }



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
      manager.openCamera(cameraId,cameraStateCallback,cameraHandler)

    }catch (e:Exception){
      _cameraState.update { it.copy(msg = "checkSelfPermission:  $e") }
    }
    //_cameraState.update { it.copy(msg = "end camera initial") }

  }


  private fun startBackgroundThread(){
    cameraHandlerThread= HandlerThread("CameraVideoThread")
    cameraHandlerThread.start()
    cameraHandler= Handler(cameraHandlerThread.looper)
  }

  private fun stopBackgroundThread(){
    cameraHandlerThread.quitSafely()
    cameraHandlerThread.join()
  }

  private val cameraStateCallback = object :CameraDevice.StateCallback(){
    override fun onOpened(p0: CameraDevice) {
      mCameraDevice=p0
      createCameraPreviewSession()
     // _cameraState.update { it.copy(msg = "CameraDevice.StateCallback onOpened") }
    }

    override fun onDisconnected(p0: CameraDevice) {
      _cameraState.update { it.copy(msg = "onDisconnected") }
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
    mSufaceTexture=p0
    mPreviewSize= Size(width,height)
    initial()
  }

  override fun closeCamera(onError:(Exception)->Unit){

  }

  private fun createCameraPreviewSession() {
    try {
      val texture: SurfaceTexture = mSufaceTexture
// 创建和设置 SessionConfiguration

      // We configure the size of default buffer to be the size of camera preview we want.
      texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight())

      // This is the output Surface we need to start preview.
      val surface = Surface(texture)

      // We set up a CaptureRequest.Builder with the output Surface.
      val mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
      mPreviewRequestBuilder.addTarget(surface)

      val mPreviewCaptureRequest = mPreviewRequestBuilder.build()
//      createCaptureSession(List<Surface> outputs, CameraCaptureSession.StateCallback callback, Handler handler)
//      This method was deprecated in API level 30. Please use createCaptureSession(android.hardware.camera2.params.SessionConfiguration) for the full set of configuration options available.
      val outputConfigurations = ArrayList<OutputConfiguration>()
      //SessionConfiguration(int sessionType, List<OutputConfiguration> outputs, Executor executor, CameraCaptureSession.StateCallback cb)
      // Here, we create a CameraCaptureSession for camera preview.

      //prepare the output surface
      val outPutSurfaces=listOf(surface)


     mCameraDevice.createCaptureSession(
        outPutSurfaces,
        object : CameraCaptureSession.StateCallback() {
          override fun onConfigured(session: CameraCaptureSession) {
            // 会话已经配置好，你可以开始捕获图像了
            // 保存 CameraCaptureSession 对象以便后续使用
            session.setRepeatingBurst(listOf(mPreviewCaptureRequest),null,null)
          }

          override fun onConfigureFailed(session: CameraCaptureSession) {
            // 会话配置失败，处理错误
            _cameraState.update { it.copy(msg = "onConfigureFailed") }
          }
        },
        cameraHandler
      )
    } catch (e: CameraAccessException) {
      _cameraState.update { it.copy(msg= "createCameraPreviewSession: CameraAccessException $e") }
      e.printStackTrace()
    }
    _cameraState.update { it.copy(msg= "end createCameraPreviewSession") }



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

