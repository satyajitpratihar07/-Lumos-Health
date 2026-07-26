package com.presagetech.smartspectra

import androidx.camera.core.Preview
import com.presagetech.smartspectra.proto.MetricTypesProto.MetricType

class SmartSpectraConfig {
    var apiKey: String = ""
    var imageOutputEnabled: Boolean = false
    var cameraPosition: CameraPosition = CameraPosition.FRONT
    var previewSurfaceProvider: Preview.SurfaceProvider? = null
    var requestedMetrics: List<MetricType> = emptyList()

    companion object {
        val breathingMetrics = listOf(MetricType.BREATHING)
        val cardioMetrics = listOf(MetricType.CARDIO)
    }
}
