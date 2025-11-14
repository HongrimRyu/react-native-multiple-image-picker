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
        val camera = SimpleCameraX.of() ?: return

        camera.setImageEngine { context, url, imageView ->
            if (context != null && url != null && imageView != null) {
                Glide.with(context).load(url).into(imageView)
            }
        }

        camera.isAutoRotation(true)
        camera.setCameraMode(cameraMode)
        camera.isDisplayRecordChangeTime(true)
        camera.isManualFocusCameraPreview(true)
        camera.isZoomCameraPreview(true)

        val maxDuration = config.videoMaximumDuration?.toInt() ?: 60
        camera.setRecordVideoMaxSecond(maxDuration)

        val isFrontCamera = config.cameraDevice == CameraDevice.FRONT
        camera.setCameraAroundState(isFrontCamera)

        camera.setOutputPathDir(getSandboxCameraOutputPath())

        config.color?.let { colorValue ->
            try {
                val primaryColor = ColorPropConverter.getColor(colorValue, appContext)
                camera.setCaptureLoadingColor(primaryColor)
            } catch (_: Exception) {}
        }

        fragment.activity?.let { activity ->
            camera.start(activity, fragment, requestCode)
        }
    }

    private fun getSandboxCameraOutputPath(): String {
        val baseDir = appContext.getExternalFilesDir("")?.absolutePath ?: appContext.filesDir.absolutePath
        val customFile = File(baseDir, "Sandbox")
        if (!customFile.exists()) customFile.mkdirs()
        return "${customFile.absolutePath}${File.separator}"
    }
}
