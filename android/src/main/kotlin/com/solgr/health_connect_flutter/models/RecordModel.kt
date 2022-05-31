package com.solgr.health_connect_flutter.models

import java.time.Instant


class RecordModel(
    val value: Double,
    val startDate: Instant,
    val endDate: Instant,
    val unit: RecordUnitEnum,
    val recordTypeEnum: RecordTypeEnum,
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "value" to value,
            "startDate" to startDate.toString(),
            "endDate" to endDate.toString(),
            "unit" to unit.ordinal,
            "recordType" to recordTypeEnum.ordinal,
        )
    }
}