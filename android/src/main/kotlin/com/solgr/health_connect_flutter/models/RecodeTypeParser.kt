package com.solgr.health_connect_flutter.models

import androidx.health.connect.client.records.Height
import androidx.health.connect.client.records.Weight

class RecodeTypeParser {

    private var WEIGHT = "WEIGHT"
    private var HEIGHT = "HEIGHT"

       fun fromString(key:String): Any? {
        return when (key) {
            WEIGHT -> Weight::class
            HEIGHT -> Height::class
            else -> null
        }
    }
}