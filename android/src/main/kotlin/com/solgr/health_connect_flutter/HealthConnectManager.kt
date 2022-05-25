/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.solgr.health_connect_flutter

import android.content.Context
import android.os.Build
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthDataRequestPermissions
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.Weight
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

// The minimum android level that can use Health Connect
const val MIN_SUPPORTED_SDK = Build.VERSION_CODES.O_MR1
var  HealthConnectavailability = HealthConnectAvailability.NOT_SUPPORTED

/**
 * Demonstrates reading and writing from Health Connect.
 */
class HealthConnectManager(private val context: Context) {
//    private lateinit var healthConnectClient : HealthConnectClient
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }



    init {
        HealthConnectavailability = when {
            HealthConnectClient.isAvailable(context) -> HealthConnectAvailability.INSTALLED
            isSupported() -> HealthConnectAvailability.NOT_INSTALLED
            else -> HealthConnectAvailability.NOT_SUPPORTED
        }
    }

    /**
     * Determines whether all the specified permissions are already granted. It is recommended to
     * call [PermissionController.getGrantedPermissions] first in the permissions flow, as if the
     * permissions are already granted then there is no need to request permissions via
     * [HealthDataRequestPermissions].
     */
    suspend fun hasAllPermissions(permissions: Set<Permission>): Boolean {
        val granted = healthConnectClient.permissionController.getGrantedPermissions(permissions)
        return granted.containsAll(permissions)
    }

    /**
     * Writes [Weight] record to Health Connect.
     */
    suspend fun writeWeightInput(weight: Weight) {
        val records = listOf(weight)
        healthConnectClient.insertRecords(records)
    }

    /**
     * Reads in existing [Weight] records.
     */
    suspend fun readWeightInputs(start: Instant, end: Instant): List<Weight> {
        val request = ReadRecordsRequest(
            recordType = Weight::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records
    }

    /**
     * Returns the weekly average of [Weight] records.
     */
    suspend fun computeWeeklyAverage(start: Instant, end: Instant): Double? {
        val request = AggregateRequest(
            metrics = setOf(Weight.WEIGHT_AVG),
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.aggregate(request)
        return response.getMetric(Weight.WEIGHT_AVG)
    }

    /**
     * Deletes a [Weight] record.
     */
    suspend fun deleteWeightInput(uid: String) {
        healthConnectClient.deleteRecords(
            Weight::class,
            uidsList = listOf(uid),
            clientIdsList = emptyList()
        )
    }


    private fun isSupported() = android.os.Build.VERSION.SDK_INT >= MIN_SUPPORTED_SDK
}

/**
 * Health Connect requires that the underlying Healthcore APK is installed on the device.
 * [HealthConnectAvailability] represents whether this APK is indeed installed, whether it is not
 * installed but supported on the device, or whether the device is not supported (based on Android
 * version).
 */
enum class HealthConnectAvailability {
    INSTALLED,
    NOT_INSTALLED,
    NOT_SUPPORTED
}
