package com.solgr.health_connect_flutter

import android.content.Context
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.response.ReadRecordsResponse
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.units.*
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

    /** * getTotal [Steps] between tow dates. */
    suspend fun getTotalSteps(start: Instant, end: Instant): Long {
        val response =
            healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
        // The result may be null if no data is available in the time range.
        return response[StepsRecord.COUNT_TOTAL] ?: 0
    }

    /** * getTotal [ActivitySession] between tow dates. */
    suspend fun getTotalActivitySession(start: Instant, end: Instant): Long {
        val response =
            healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(ExerciseSessionRecord.ACTIVE_TIME_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
        // The result may be null if no data is available in the time range.
        return response[ExerciseSessionRecord.ACTIVE_TIME_TOTAL]?.toMinutes() ?: 0
    }

    /**
     * Reads records.
     */
    suspend fun readRecords(
        recordType: RecordTypeEnum,
        startTime: Instant,
        endTime: Instant
    ): List<RecordModel> {
        return when (recordType) {
            RecordTypeEnum.WEIGHT -> return readWeight(startTime, endTime)
            RecordTypeEnum.ACTIVE_CALORIES_BURNED -> return readActiveCaloriesBurnedInputs(startTime, endTime)
            RecordTypeEnum.ACTIVITY_SESSION -> return readActivitySession(startTime, endTime)
            RecordTypeEnum.BASAL_BODY_TEMPERATURE -> return readBasalBodyTemperature(startTime, endTime)
            RecordTypeEnum.BLOOD_GLUCOSE -> return readBloodGlucose(startTime, endTime)
            RecordTypeEnum.BLOOD_PRESSURE -> return readBloodPressure(startTime, endTime)
            RecordTypeEnum.BODY_FAT -> return readBodyFat(startTime, endTime)
            RecordTypeEnum.HEART_RATE -> return readHeartRate(startTime, endTime)
            RecordTypeEnum.HEIGHT -> return readHeight(startTime, endTime)
            RecordTypeEnum.STEPS -> return readSteps(startTime, endTime)
            RecordTypeEnum.HYDRATION -> return readHydration(startTime, endTime)
            else -> emptyList()
        }
    }

    suspend fun <T : Record> readRecords2(request: ReadRecordsRequest<T>): ReadRecordsResponse<T> {
        return healthConnectClient.readRecords(request)
    }

    /** * Reads in existing [Hydration] records. */
    private suspend fun readHydration(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = HydrationRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.volume.toString(),
                record.startTime,
                record.endTime,
                RecordUnitEnum.LITER,
                RecordTypeEnum.HYDRATION,
                record.metadata,
            )
        }
    }

    /** * Reads in existing [Steps] records. */
    private suspend fun readSteps(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
                recordType = StepsRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.count.toString(),
                record.startTime,
                record.endTime,
                RecordUnitEnum.COUNT,
                RecordTypeEnum.STEPS,
                record.metadata,
            )
        }
    }


    /** * Reads in existing [Height] records. */
    private suspend fun readHeight(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = HeightRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.height.toString(),
                record.time,
                record.time,
                RecordUnitEnum.METERS,
                RecordTypeEnum.HEIGHT,
                record.metadata,
            )
        }
    }

    /** * Reads in existing [HeartRate] records. */
    private suspend fun readHeartRate(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = HeartRateRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.samples.map { heartRate -> heartRate.beatsPerMinute }.average().toString(),
                record.startTime,
                record.endTime,
                RecordUnitEnum.BEATS_PER_MINUTE,
                RecordTypeEnum.HEART_RATE,
                record.metadata,
            )
        }
    }

    /** * Reads in existing [BodyFat] records. */
    private suspend fun readBodyFat(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = BodyFatRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.percentage.toString(),
                record.time,
                record.time,
                RecordUnitEnum.PERCENTAGE,
                RecordTypeEnum.BODY_FAT,
                record.metadata,
            )

        }
    }

    /** * Reads in existing [BloodPressure] records. */
    private suspend fun readBloodPressure(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = BloodPressureRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.diastolic.inMillimetersOfMercury.toString(),
                record.time,
                record.time,
                RecordUnitEnum.MILLIMETER_OF_MERCURY,
                RecordTypeEnum.BLOOD_GLUCOSE,
                record.metadata,
            )

        }
    }

    /** * Reads in existing [BloodGlucose] records. */
    private suspend fun readBloodGlucose(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = BloodGlucoseRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.levelMillimolesPerLiter.toString(),
                record.time,
                record.time,
                RecordUnitEnum.MILLIGRAM_PER_DECILITER,
                RecordTypeEnum.BLOOD_GLUCOSE,
                record.metadata,
            )

        }
    }

    /** * Reads in existing [BasalBodyTemperature] records. */
    private suspend fun readBasalBodyTemperature(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = BasalBodyTemperatureRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.temperature.inCelsius.toString(),
                record.time,
                record.time,
                RecordUnitEnum.DEGREE_CELSIUS,
                RecordTypeEnum.BASAL_BODY_TEMPERATURE,
                record.metadata,
            )

        }
    }

    /** * Reads in existing [ActivitySession] records. */
    private suspend fun readActivitySession(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = ExerciseSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.exerciseType,
                record.startTime,
                record.endTime,
                RecordUnitEnum.STRING_TYPE,
                RecordTypeEnum.ACTIVITY_SESSION,
                record.metadata,
            )

        }
    }

    /** * Reads in existing [ActiveCaloriesBurned] records. */
    private suspend fun readActiveCaloriesBurnedInputs(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = ActiveCaloriesBurnedRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.energy.inKilocalories.toString(),
                record.startTime,
                record.endTime,
                RecordUnitEnum.CALORIES,
                RecordTypeEnum.ACTIVE_CALORIES_BURNED,
                record.metadata,
            )
        }
    }

    /** * Reads in existing [Weight] records. */
    private suspend fun readWeight(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = WeightRecord::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.weight.inKilograms.toString(),
                record.time,
                record.time,
                RecordUnitEnum.KILOGRAMS,
                RecordTypeEnum.WEIGHT,
                record.metadata,
            )
        }
    }


    /**
     * Writes records.
     */
    suspend fun writeRecords(value: String, recordType: RecordTypeEnum, startTime: Instant, endTime: Instant): Boolean {
        return when (recordType) {
            RecordTypeEnum.WEIGHT -> return writeWeightRecord(value, startTime)
            RecordTypeEnum.ACTIVE_CALORIES_BURNED -> return writeActiveCaloriesBurned(value, startTime, endTime)
            RecordTypeEnum.ACTIVITY_SESSION -> return writeActivitySession(value, startTime, endTime)
            RecordTypeEnum.BASAL_BODY_TEMPERATURE -> return writeBasalBodyTemperature(value, startTime, endTime)
            RecordTypeEnum.BLOOD_GLUCOSE -> return writeBloodGlucose(value, startTime, endTime)
            RecordTypeEnum.BLOOD_PRESSURE -> return writeBloodPressure(value, startTime, endTime)
            RecordTypeEnum.BODY_FAT -> return writeBodyFat(value, startTime, endTime)
            RecordTypeEnum.HEIGHT -> return writeHeight(value, startTime, endTime)
            RecordTypeEnum.STEPS -> return writeSteps(value, startTime, endTime)
            RecordTypeEnum.HYDRATION -> return writeHydration(value, startTime, endTime)
            else -> false
        }
    }

    /** * Writes in [Hydration] records. */
    private suspend fun writeHydration(value: String, startTime: Instant, endTime: Instant): Boolean {
        checkParseDoubleValue(value)
        val records = listOf(
            HydrationRecord(
                Volume.liters(value.toDouble()) ,
                startTime = startTime,
                endTime = endTime.plusMillis(1),
                startZoneOffset = null,
                endZoneOffset = null,
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [Steps] records. */
    private suspend fun writeSteps(value: String, startTime: Instant, endTime: Instant): Boolean {
        checkParseDoubleValue(value)
        val records = listOf(
            StepsRecord(
                value.toDouble().toLong(),
                startTime = startTime,
                endTime = endTime.plusMillis(1),
                startZoneOffset = null,
                endZoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [Height] records. */
    private suspend fun writeHeight(value: String, startTime: Instant, endTime: Instant): Boolean {
        checkParseDoubleValue(value)
        val records = listOf(
            HeightRecord(
                height =Length.meters(value.toDouble())  ,
                time = startTime,
                zoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [BodyFat] records. */
    private suspend fun writeBodyFat(value: String, startTime: Instant, endTime: Instant): Boolean {
        checkParseDoubleValue(value)
        val records = listOf(
            BodyFatRecord(
                percentage =  Percentage(value.toDouble())   ,
                time = startTime,
                zoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [BloodPressure] records. */
    private suspend fun writeBloodPressure(value: String, startTime: Instant, endTime: Instant): Boolean {
        checkParseDoubleValue(value)
        val records = listOf(
            BloodPressureRecord(
                systolic = Pressure.millimetersOfMercury( value.toDouble()),
                diastolic= Pressure.millimetersOfMercury( value.toDouble()),
                null,// TODO:
                null,// TODO:
                time = startTime,
                zoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [BloodGlucose] records. */
    private suspend fun writeBloodGlucose(value: String, startTime: Instant, endTime: Instant): Boolean {
        checkParseDoubleValue(value)
        val records = listOf(
            BloodGlucoseRecord(
                value.toDouble(),
                null,// TODO:
                null,// TODO:
                null,// TODO:
                time = startTime,
                zoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [BasalBodyTemperature] records. */
    private suspend fun writeBasalBodyTemperature(value: String, startTime: Instant, endTime: Instant): Boolean {
        checkParseDoubleValue(value)
        val records = listOf(
            BasalBodyTemperatureRecord(
                Temperature.celsius(value.toDouble()) ,
                measurementLocation = null,// TODO:
                time = startTime,
                zoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [ActivitySession] records. */
    private suspend fun writeActivitySession(value: String, startTime: Instant, endTime: Instant): Boolean {
        // TODO: activityType : https://developer.android.com/reference/kotlin/androidx/health/connect/client/records/ActivitySession.ActivityType#BACK_EXTENSION:kotlin.String
        val records = listOf(
            ExerciseSessionRecord(
                value,
                startTime = startTime,
                endTime = endTime.plusMillis(1),
                startZoneOffset = null,
                endZoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [ActiveCaloriesBurned] records. */
    private suspend fun writeActiveCaloriesBurned(value: String, startTime: Instant, endTime: Instant): Boolean {
        checkParseDoubleValue(value)
        val records = listOf(
            ActiveCaloriesBurnedRecord(
                Energy.calories(value.toDouble()) ,
                startTime = startTime,
                endTime = endTime.plusMillis(1),
                startZoneOffset = null,
                endZoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [Weight] records. */
    private suspend fun writeWeightRecord(value: String, startTime: Instant): Boolean {
        checkParseDoubleValue(value)
        val records = listOf(
            WeightRecord(
                Mass.kilograms(value.toDouble()) ,
                time = startTime,
                zoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    private fun checkParseDoubleValue(value: String) {
        if (value.toDoubleOrNull() == null) {
            throw Exception("error with parsing value to double.")
        }
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
