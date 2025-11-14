package com.margelo.nitro.multipleimagepicker

import android.content.Context
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.facebook.react.bridge.ColorPropConverter
import com.luck.lib.camerax.SimpleCameraX
import com.luck.picture.lib.interfaces.OnCameraInterceptListener
import java.io.File

class CameraEngine(
    private val appContext: Context,
    private val config: NitroCameraConfig,
) : OnCameraInterceptListener {

    override fun openCamera(fragment: Fragment, cameraMode: Int, requestCode: Int) {
        val camera = SimpleCameraX.of()

        // 이미지 엔진 설정 - null 안전성 보장
        camera.setImageEngine { context, url, imageView ->
            if (context != null && url != null && imageView != null) {
                Glide.with(context).load(url).into(imageView)
            }
        }

        // 카메라 설정
        camera.isAutoRotation(true)
        camera.setCameraMode(cameraMode)
        camera.isDisplayRecordChangeTime(true)
        camera.isManualFocusCameraPreview(true)
        camera.isZoomCameraPreview(true)

        // 비디오 최대 시간 설정 (null 안전)
        val maxDuration = config.videoMaximumDuration?.toInt() ?: 60
        camera.setRecordVideoMaxSecond(maxDuration)

        // 카메라 방향 설정
        val isFrontCamera = config.cameraDevice == CameraDevice.FRONT
        camera.setCameraAroundState(isFrontCamera)

        // 출력 경로 설정
        camera.setOutputPathDir(getSandboxCameraOutputPath())

        // 색상 설정 (null 안전)
        config.color?.let { colorValue ->
            try {
                val primaryColor = ColorPropConverter.getColor(colorValue, appContext)
                camera.setCaptureLoadingColor(primaryColor)
            } catch (e: Exception) {
                // 색상 변환 실패 시 기본값 사용
                e.printStackTrace()
            }
        }

        // 카메라 시작 (null 체크)
        val activity = fragment.activity
        if (activity != null) {
            camera.start(activity, fragment, requestCode)
        }
    }

    private fun getSandboxCameraOutputPath(): String {
        val externalFilesDir: File? = appContext.getExternalFilesDir("")

        // null 안전성을 보장하는 경로 생성
        val baseDir = externalFilesDir?.absolutePath ?: appContext.filesDir.absolutePath
        val customFile = File(baseDir, "Sandbox")

        if (!customFile.exists()) {
            customFile.mkdirs()
        }

        return "${customFile.absolutePath}${File.separator}"
    }
}
