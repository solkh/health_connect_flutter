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
import android.util.Log
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.metadata.Metadata
import androidx.health.connect.client.permission.HealthDataRequestPermissions
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.Weight
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.response.InsertRecordsResponse
import androidx.health.connect.client.time.TimeRangeFilter
import com.solgr.health_connect_flutter.models.RecordModel
import com.solgr.health_connect_flutter.models.RecordTypeEnum
import java.time.Instant
import java.util.*

// The minimum android level that can use Health Connect
const val MIN_SUPPORTED_SDK = Build.VERSION_CODES.O_MR1
var HealthConnectAvailability = HealthConnectAvailabilityStatus.NOT_SUPPORTED

/**
 * Demonstrates reading and writing from Health Connect.
 */
class HealthConnectManager(private val context: Context) {
    val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    init {
        Log.i(
            TAG,
            "%%%%%%%%%%%%%%%%%%%%%%%%%%%% HealthConnectClient.isAvailable : ${
                HealthConnectClient.isAvailable(context)
            }"
        )
        HealthConnectAvailability = when {
            HealthConnectClient.isAvailable(context) -> HealthConnectAvailabilityStatus.INSTALLED
            isSupported() -> HealthConnectAvailabilityStatus.NOT_INSTALLED
            else -> HealthConnectAvailabilityStatus.NOT_SUPPORTED
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


    @ChecksSdkIntAtLeast(api = MIN_SUPPORTED_SDK)
    private fun isSupported() =  Build.VERSION.SDK_INT >= MIN_SUPPORTED_SDK



    /**
     * Reads records.
     */
    suspend fun readRecords(  recordType : RecordTypeEnum, startTime: Instant, endTime: Instant) : List<RecordModel>{
        return when (recordType){
            RecordTypeEnum.Weight -> return readWeightInputs(startTime,endTime )
            else -> emptyList()
        }
    }

    /** * Reads in existing [Weight] records. */
   private suspend fun readWeightInputs(start: Instant, end: Instant): List<RecordModel> {
        println("recordType")
        val request = ReadRecordsRequest( recordType = Weight::class, timeRangeFilter = TimeRangeFilter.between(start, end))
        val response = healthConnectClient.readRecords(request)
        return response.records.map { weight -> RecordModel(weight.weightKg, weight.time, "KG",RecordTypeEnum.Weight)}
    }



    /**
     * Writes records.
     */
    suspend fun writeRecords( value : Double, recordType : RecordTypeEnum, createDate: Instant ) : Boolean{
        return when (recordType){
            RecordTypeEnum.Weight -> return writeWeightRecord(value,createDate )
            else -> false
        }
    }

    /** * Writes in [Weight] records. */
    suspend fun writeWeightRecord(value : Double,createDate: Instant): Boolean {
        val records = listOf(Weight(
            value,
            time = createDate,
            zoneOffset = null,
            metadata = Metadata(UUID.randomUUID().toString())
        ))
      return  healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }
}

/**
 * Health Connect requires that the underlying Healthcore APK is installed on the device.
 * [HealthConnectAvailability] represents whether this APK is indeed installed, whether it is not
 * installed but supported on the device, or whether the device is not supported (based on Android
 * version).
 */
enum class HealthConnectAvailabilityStatus {
    INSTALLED,
    NOT_INSTALLED,
    NOT_SUPPORTED
}
