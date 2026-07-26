package com.presagetech.smartspectra

enum class ValidationCode {
    OK,
    NO_FACE_DETECTED,
    TOO_FAR,
    TOO_CLOSE,
    POOR_LIGHTING,
    EXCESSIVE_MOTION
}

data class ValidationStatus(
    val code: ValidationCode = ValidationCode.OK
)
