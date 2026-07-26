package com.presagetech.smartspectra.proto

object MetricTypesProto {
    enum class MetricType {
        BREATHING,
        CARDIO,
        EXPRESSIONS
    }
}

object MetricsProto {
    enum class ExpressionType {
        ANGRY,
        CONTEMPT,
        DISGUST,
        FEAR,
        HAPPY,
        NEUTRAL,
        SAD,
        SURPRISE
    }
}
