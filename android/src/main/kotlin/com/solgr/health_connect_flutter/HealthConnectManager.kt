package com.solgr.health_connect_flutter

import android.content.Context
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.metadata.Metadata
import androidx.health.connect.client.permission.HealthDataRequestPermissions
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.ActiveCaloriesBurned
import androidx.health.connect.client.records.ActiveEnergyBurned
import androidx.health.connect.client.records.Weight
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.solgr.health_connect_flutter.models.RecordModel
import com.solgr.health_connect_flutter.models.RecordTypeEnum
import com.solgr.health_connect_flutter.models.RecordUnitEnum
import java.time.Instant
import java.util.*

// The minimum android level that can use Health Connect
const val MIN_SUPPORTED_SDK = Build.VERSION_CODES.O_MR1
var HealthConnectAvailability = HealthConnectAvailabilityStatus.NOT_SUPPORTED

/**
 * Demonstrates reading and writing from Health Connect.
 */
class HealthConnectManager(private val context: Context) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    init {
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
    private fun isSupported() = Build.VERSION.SDK_INT >= MIN_SUPPORTED_SDK


    /**
     * Reads records.
     */
    suspend fun readRecords(
        recordType: RecordTypeEnum,
        startTime: Instant,
        endTime: Instant
    ): List<RecordModel> {
        return when (recordType) {
            RecordTypeEnum.Weight -> return readWeightInputs(startTime, endTime)
            RecordTypeEnum.ACTIVE_ENERGY_BURNED -> return readActiveEnergyBurnedInputs(
                startTime,
                endTime
            )
            RecordTypeEnum.ACTIVE_CALORIES_BURNED -> return readActiveCaloriesBurnedInputs(
                startTime,
                endTime
            )
            else -> emptyList()
        }
    }

    /** * Reads in existing [ActiveCaloriesBurned] records. */
    private suspend fun readActiveCaloriesBurnedInputs(
        start: Instant,
        end: Instant
    ): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = ActiveCaloriesBurned::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.energyKcal,
                record.startTime,
                record.endTime,
                RecordUnitEnum.CALORIES,
                RecordTypeEnum.ACTIVE_CALORIES_BURNED
            )
        }
    }

    /** * Reads in existing [ActiveEnergyBurned] records. */
    private suspend fun readActiveEnergyBurnedInputs(
        start: Instant,
        end: Instant
    ): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = ActiveEnergyBurned::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.energyKcal,
                record.startTime,
                record.endTime,
                RecordUnitEnum.CALORIES,
                RecordTypeEnum.ACTIVE_ENERGY_BURNED
            )
        }
    }

    /** * Reads in existing [Weight] records. */
    private suspend fun readWeightInputs(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = Weight::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.weightKg,
                record.time,
                record.time,
                RecordUnitEnum.KILOGRAMS,
                RecordTypeEnum.Weight
            )
        }
    }


    /**
     * Writes records.
     */
    suspend fun writeRecords(
        value: Double,
        recordType: RecordTypeEnum,
        createDate: Instant
    ): Boolean {
        return when (recordType) {
            RecordTypeEnum.Weight -> return writeWeightRecord(value, createDate)
            RecordTypeEnum.ACTIVE_ENERGY_BURNED -> return writeActiveEnergyBurnedRecord(
                value,
                createDate
            )
            RecordTypeEnum.ACTIVE_CALORIES_BURNED -> return writeActiveCaloriesBurnedRecord(
                value,
                createDate
            )
            else -> false
        }
    }

    /** * Writes in [ActiveCaloriesBurned] records. */
    private suspend fun writeActiveCaloriesBurnedRecord(
        value: Double,
        createDate: Instant
    ): Boolean {
        val records = listOf(
            ActiveCaloriesBurned(
                value,
                startTime = createDate,
                endTime = createDate.plusMillis(1),
                startZoneOffset = null,
                endZoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [ActiveEnergyBurned] records. */
    private suspend fun writeActiveEnergyBurnedRecord(value: Double, createDate: Instant): Boolean {
        val records = listOf(
            ActiveEnergyBurned(
                value,
                startTime = createDate,
                endTime = createDate.plusMillis(1),
                startZoneOffset = null,
                endZoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [Weight] records. */
    private suspend fun writeWeightRecord(value: Double, createDate: Instant): Boolean {
        val records = listOf(
            Weight(
                value,
                time = createDate,
                zoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }
}

/**
 * Health Connect requires that the underlying Health core APK is installed on the device.
 * [HealthConnectAvailability] represents whether this APK is indeed installed, whether it is not
 * installed but supported on the device, or whether the device is not supported (based on Android
 * version).
 */
enum class HealthConnectAvailabilityStatus {
    INSTALLED,
    NOT_INSTALLED,
    NOT_SUPPORTED
}
