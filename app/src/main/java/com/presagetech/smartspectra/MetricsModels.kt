package com.presagetech.smartspectra

import com.presagetech.smartspectra.proto.MetricsProto.ExpressionType

data class PulseRateSample(
    val timestamp: Long = System.currentTimeMillis(),
    val value: Float = 72f
)

data class TraceSample(
    val timestamp: Long = System.currentTimeMillis(),
    val value: Float = 50f
)

data class HrvSample(
    val timestamp: Long = System.currentTimeMillis(),
    val rmssd: Float = 45f
)

data class CardioMetrics(
    val pulseRateList: List<PulseRateSample> = listOf(PulseRateSample()),
    val arterialPressureTraceList: List<TraceSample> = emptyList(),
    val hrvList: List<HrvSample> = listOf(HrvSample())
)

data class BreathingRateSample(
    val timestamp: Long = System.currentTimeMillis(),
    val value: Float = 16f
)

data class BreathingMetrics(
    val rateCount: Int = 1,
    val rateList: List<BreathingRateSample> = listOf(BreathingRateSample()),
    val upperTraceList: List<TraceSample> = emptyList(),
    val lowerTraceList: List<TraceSample> = emptyList()
)

data class ExpressionScore(
    val type: ExpressionType = ExpressionType.NEUTRAL,
    val confidence: Float = 90f
)

data class ExpressionSample(
    val timestamp: Long = System.currentTimeMillis(),
    val scoresList: List<ExpressionScore> = listOf(ExpressionScore())
)

data class FaceMetrics(
    val expressionList: List<ExpressionSample> = listOf(ExpressionSample())
)

data class SmartSpectraMetrics(
    val cardio: CardioMetrics = CardioMetrics(),
    val breathing: BreathingMetrics = BreathingMetrics(),
    val face: FaceMetrics = FaceMetrics()
) {
    fun hasCardio(): Boolean = true
    fun hasBreathing(): Boolean = true
    fun hasFace(): Boolean = true
}
