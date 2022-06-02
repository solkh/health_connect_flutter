package com.solgr.health_connect_flutter.models

import java.time.Instant
import androidx.health.connect.client.metadata.Metadata


class RecordModel(
    val value: String,
    val startTime: Instant,
    val endTime: Instant,
    val unit: RecordUnitEnum,
    val recordTypeEnum: RecordTypeEnum,
    val metadata : Metadata,
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "value" to value,
            "startTime" to startTime.toString(),
            "endTime" to endTime.toString(),
            "unit" to unit.ordinal,
            "recordType" to recordTypeEnum.ordinal,
        )
    }
}