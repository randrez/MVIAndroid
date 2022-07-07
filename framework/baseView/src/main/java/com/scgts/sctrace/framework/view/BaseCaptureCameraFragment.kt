package com.scgts.sctrace.framework.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.scandit.datacapture.barcode.capture.BarcodeCapture
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings
import com.scandit.datacapture.barcode.data.Symbology.*
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay
import com.scandit.datacapture.core.area.RectangularLocationSelection
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.common.feedback.Feedback
import com.scandit.datacapture.core.common.geometry.Anchor
import com.scandit.datacapture.core.common.geometry.MeasureUnit
import com.scandit.datacapture.core.common.geometry.PointWithUnit
import com.scandit.datacapture.core.common.geometry.SizeWithUnit
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.core.source.CameraPosition
import com.scandit.datacapture.core.source.FrameSourceState
import com.scandit.datacapture.core.ui.DataCaptureView
import com.scandit.datacapture.core.ui.style.Brush
import org.koin.android.ext.android.get
import org.koin.core.qualifier.named

abstract class BaseCaptureCameraFragment<VM : ViewModel> : BaseFragment<VM>(),
    BarcodeCaptureListener {

    companion object {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val CAMERA_PERMISSION_REQUEST = 0
        const val SCANDIT_LICENSE_KEY = "scandit license key"
        const val DIRECT_PART_MARKING_MODE = "direct_part_marking_mode"
    }

    private var dataCaptureContext: DataCaptureContext? = null
    private var barcodeCapture: BarcodeCapture? = null
    private var camera: Camera? = null
    protected lateinit var dataCaptureView: DataCaptureView
    protected var tagDataCaptureView: DataCaptureView? = null
    private lateinit var overlay: BarcodeCaptureOverlay
    private val feedback = Feedback.defaultFeedback()

    private var permissionDeniedOnce = false
    private var paused = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    private fun setUp() {
        val scanditLicenseKey: String = get(named(SCANDIT_LICENSE_KEY))
        dataCaptureContext = DataCaptureContext.forLicenseKey(scanditLicenseKey)

        camera = Camera.getCamera(CameraPosition.WORLD_FACING)
        if (camera != null) {
            camera!!.applySettings(BarcodeCapture.createRecommendedCameraSettings())
            dataCaptureContext!!.setFrameSource(camera)
        } else {
            throw IllegalStateException(
                "Sample depends on a camera, which failed to initialize."
            )
        }

        val barcodeCaptureSettings = BarcodeCaptureSettings()

        barcodeCaptureSettings.enableSymbologies(
            setOf(UPCE, EAN8, DATA_MATRIX, QR, EAN13_UPCA, CODE128)
        )
        barcodeCaptureSettings.getSymbologySettings(DATA_MATRIX)
            .setExtensionEnabled(DIRECT_PART_MARKING_MODE, true)
        barcodeCaptureSettings.getSymbologySettings(DATA_MATRIX).isColorInvertedEnabled = true
        barcodeCaptureSettings.locationSelection =
            RectangularLocationSelection.withSize(SizeWithUnit(0.4f, 0.2f, MeasureUnit.FRACTION))

        barcodeCapture =
            BarcodeCapture.forDataCaptureContext(dataCaptureContext, barcodeCaptureSettings)

        barcodeCapture!!.feedback.success = Feedback()

        barcodeCapture!!.addListener(this)
        dataCaptureView.dataCaptureContext = dataCaptureContext

        dataCaptureView.logoAnchor = Anchor.TOP_RIGHT
        dataCaptureView.pointOfInterest = PointWithUnit(0.5f, 0.25f, MeasureUnit.FRACTION)
        overlay = BarcodeCaptureOverlay.newInstance(barcodeCapture!!, dataCaptureView)
    }

    override fun onBarcodeScanned(
        barcodeCapture: BarcodeCapture,
        session: BarcodeCaptureSession,
        data: FrameData,
    ) {
        if (session.newlyRecognizedBarcodes.isEmpty()) return
        pauseFrameSource()

        val barcode = session.newlyRecognizedBarcodes[0]
        if (barcode.data == null) {
            overlay.brush = Brush.transparent()
            return
        }
        overlay.brush = BarcodeCaptureOverlay.defaultBrush()
        feedback.emit()
        barcodeCapture.isEnabled = false

        barcode.data?.let {
            onBarcodeParsed(
                if (barcode.symbology == EAN13_UPCA) {
                    it.substring(it.length - barcode.symbolCount)
                } else {
                    it
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        paused = false
        requestCameraPermission()
    }

    protected open fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    protected open fun requestCameraPermission() {
        // For Android M and onwards we need to request the camera permission from the user.
        if (!hasCameraPermission()) {
            // The user already denied the permission once, we don't ask twice.
            if (!permissionDeniedOnce) {
                // It's clear why the camera is required. We don't need to give a detailed reason.
                requestPermissions(
                    arrayOf(CAMERA_PERMISSION),
                    CAMERA_PERMISSION_REQUEST
                )
            }
        } else {
            // We already have the permission or don't need it.
            onCameraPermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray,
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionDeniedOnce = false
                if (!paused) {
                    // Only call the function if not paused - camera should not be used otherwise.
                    onCameraPermissionGranted()
                }
            } else {
                // The user denied the permission - we are not going to ask again.
                permissionDeniedOnce = true
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onPause() {
        paused = true
        super.onPause()
    }

    protected fun resumeFrameSource() {
        camera!!.switchToDesiredState(FrameSourceState.ON, null)
        overlay.shouldShowScanAreaGuides = false
        barcodeCapture?.isEnabled = false
    }

    protected fun pauseFrameSource() {
        camera?.switchToDesiredState(FrameSourceState.STANDBY)
        overlay.shouldShowScanAreaGuides = false
        barcodeCapture?.isEnabled = false
    }

    protected fun enableBarcodeCapture() {
        camera!!.switchToDesiredState(FrameSourceState.ON, null)
        overlay.shouldShowScanAreaGuides = true
        barcodeCapture?.isEnabled = true
    }

    protected fun turnFrameSourceOff() {
        camera!!.switchToDesiredState(FrameSourceState.OFF)
        overlay.shouldShowScanAreaGuides = false
        barcodeCapture?.isEnabled = false
    }

    override fun onDestroy() {
        barcodeCapture!!.removeListener(this)
        overlay.shouldShowScanAreaGuides = false
        dataCaptureContext!!.removeMode(barcodeCapture!!)
        super.onDestroy()
    }

    abstract fun onExit()
    abstract fun onBarcodeParsed(barcode: String)
    abstract fun onCameraPermissionGranted()
}