package com.solgr.health_connect_flutter.models

<<<<<<< HEAD
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
=======
class RecordModel {
>>>>>>> 23d5414b317d8d867a60f1e8d574d13e69ebfcc9
}