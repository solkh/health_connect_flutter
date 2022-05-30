package com.solgr.health_connect_flutter.models

import java.time.Instant


class RecordModel(
    val value: Double,
    val date: Instant,
    val unit: String,
    val recordTypeEnum: RecordTypeEnum,
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "value" to value,
            "date" to date.toString(),
            "unit" to unit,
            "recordType" to recordTypeEnum.ordinal,
        )
    }
}