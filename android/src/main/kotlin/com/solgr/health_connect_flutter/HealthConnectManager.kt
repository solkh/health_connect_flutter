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
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.response.ReadRecordsResponse
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
    private  val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

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
            RecordTypeEnum.WEIGHT -> return readWeight(startTime, endTime)
            RecordTypeEnum.ACTIVE_ENERGY_BURNED -> return readActiveEnergyBurned(startTime, endTime)
            RecordTypeEnum.ACTIVE_CALORIES_BURNED -> return readActiveCaloriesBurnedInputs(startTime, endTime)
            RecordTypeEnum.ACTIVITY_EVENT -> return readActivityEvent(startTime, endTime)
            RecordTypeEnum.ACTIVITY_LAP -> return readActivityLap(startTime, endTime)
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

    suspend fun<T : Record> readRecords2 (request : ReadRecordsRequest<T>): ReadRecordsResponse<T> {
       return healthConnectClient.readRecords(request)
    }

    /** * Reads in existing [Hydration] records. */
    private suspend fun readHydration(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
                recordType = Hydration::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.volumeLiters.toString(),
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
                recordType = Steps::class,
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
                recordType = Height::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.heightMeters.toString(),
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
                recordType = HeartRateSeries::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.samples.map{ heartRate-> heartRate.beatsPerMinute }.average().toString(),
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
                recordType = BodyFat::class,
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
                recordType = BloodPressure::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.diastolicMillimetersOfMercury.toString(),
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
                recordType = BloodGlucose::class,
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
                recordType = BasalBodyTemperature::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.temperatureDegreesCelsius.toString(),
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
            recordType = ActivitySession::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.activityType,
                record.startTime,
                record.endTime,
                RecordUnitEnum.STRING_TYPE,
                RecordTypeEnum.ACTIVITY_SESSION,
                record.metadata,
            )

        }
    }

    /** * Reads in existing [ActivityLap] records. */
    private suspend fun readActivityLap(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = ActivityLap::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.lengthMeters.toString(),
                record.startTime,
                record.endTime,
                RecordUnitEnum.METERS,
                RecordTypeEnum.ACTIVITY_LAP,
                record.metadata,
            )

        }
    }
    /** * Reads in existing [ActivityEvent] records. */
    private suspend fun readActivityEvent(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = ActivityEvent::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.eventType,
                record.startTime,
                record.endTime,
                RecordUnitEnum.STRING_TYPE,
                RecordTypeEnum.ACTIVITY_EVENT,
                record.metadata,
            )

        }
    }

    /** * Reads in existing [ActiveCaloriesBurned] records. */
    private suspend fun readActiveCaloriesBurnedInputs(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = ActiveCaloriesBurned::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.energyKcal.toString(),
                record.startTime,
                record.endTime,
                RecordUnitEnum.CALORIES,
                RecordTypeEnum.ACTIVE_CALORIES_BURNED,
                record.metadata,
            )
        }
    }

    /** * Reads in existing [ActiveEnergyBurned] records. */
    private suspend fun readActiveEnergyBurned(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = ActiveEnergyBurned::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.energyKcal.toString(),
                record.startTime,
                record.endTime,
                RecordUnitEnum.CALORIES,
                RecordTypeEnum.ACTIVE_ENERGY_BURNED,
                record.metadata,
            )
        }
    }

    /** * Reads in existing [Weight] records. */
    private suspend fun readWeight(start: Instant, end: Instant): List<RecordModel> {
        val request = ReadRecordsRequest(
            recordType = Weight::class,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records.map { record ->
            RecordModel(
                record.weightKg.toString(),
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
            RecordTypeEnum.WEIGHT -> return writeWeightRecord(value , startTime)
            RecordTypeEnum.ACTIVE_ENERGY_BURNED -> return writeActiveEnergyBurned(value , startTime,endTime)
            RecordTypeEnum.ACTIVE_CALORIES_BURNED -> return writeActiveCaloriesBurned(value , startTime,endTime)
            RecordTypeEnum.ACTIVITY_EVENT -> return writeActivityEvent(value, startTime,endTime)
            RecordTypeEnum.ACTIVITY_LAP -> return writeActivityLap(value, startTime,endTime)
            RecordTypeEnum.ACTIVITY_SESSION -> return writeActivitySession(value, startTime,endTime)
            RecordTypeEnum.BASAL_BODY_TEMPERATURE -> return writeBasalBodyTemperature(value, startTime,endTime)
            RecordTypeEnum.BLOOD_GLUCOSE -> return writeBloodGlucose(value, startTime,endTime)
            RecordTypeEnum.BLOOD_PRESSURE -> return writeBloodPressure(value, startTime,endTime)
            RecordTypeEnum.BODY_FAT -> return writeBodyFat(value, startTime,endTime)
            RecordTypeEnum.HEIGHT -> return writeHeight(value, startTime,endTime)
            RecordTypeEnum.STEPS -> return writeSteps(value, startTime,endTime)
            RecordTypeEnum.HYDRATION -> return writeHydration(value, startTime,endTime)
            else -> false
        }
    }

    /** * Writes in [Hydration] records. */
    private suspend fun writeHydration(value: String, startTime: Instant,endTime : Instant): Boolean {
        checkParseDoubleValue( value   )
        val records = listOf(
            Hydration(
                value.toDouble() ,
                startTime = startTime,
                endTime = endTime.plusMillis(1),
                startZoneOffset = null,
                endZoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }
    /** * Writes in [Steps] records. */
    private suspend fun writeSteps(value: String, startTime: Instant,endTime : Instant): Boolean {
        checkParseDoubleValue( value   )
        val records = listOf(
            Steps(
                value.toDouble().toLong() ,
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
    private suspend fun writeHeight(value: String, startTime: Instant,endTime : Instant): Boolean {
        checkParseDoubleValue( value   )
        val records = listOf(
            Height(
                value.toDouble() ,
                time = startTime,
                zoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }
    /** * Writes in [BodyFat] records. */
    private suspend fun writeBodyFat(value: String, startTime: Instant,endTime : Instant): Boolean {
        checkParseDoubleValue(value)
        val records = listOf(
            BodyFat(
                value.toDouble().toInt() ,
                time = startTime,
                zoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }
    /** * Writes in [BloodPressure] records. */
    private suspend fun writeBloodPressure(value: String, startTime: Instant,endTime : Instant): Boolean {
        checkParseDoubleValue( value   )
        val records = listOf(
            BloodPressure(
                value.toDouble() ,
                value.toDouble() ,// TODO:
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
    private suspend fun writeBloodGlucose(value: String, startTime: Instant,endTime : Instant): Boolean {
        checkParseDoubleValue( value   )
        val records = listOf(
            BloodGlucose(
                value.toDouble() ,
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
    private suspend fun writeBasalBodyTemperature(value: String, startTime: Instant,endTime : Instant): Boolean {
        checkParseDoubleValue( value   )
        val records = listOf(
            BasalBodyTemperature(
                value.toDouble() ,
                measurementLocation = null,// TODO:
                time = startTime,
                zoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [ActivitySession] records. */
    private suspend fun writeActivitySession(value: String, startTime: Instant,endTime : Instant): Boolean {
        // TODO: activityType : https://developer.android.com/reference/kotlin/androidx/health/connect/client/records/ActivitySession.ActivityType#BACK_EXTENSION:kotlin.String
        val records = listOf(
            ActivitySession(
                value ,
                startTime = startTime,
                endTime = endTime.plusMillis(1),
                startZoneOffset = null,
                endZoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [ActivityLap] records. */
    private suspend fun writeActivityLap(value: String, startTime: Instant,endTime : Instant): Boolean {
        checkParseDoubleValue(value)
        val records = listOf(
            ActivityLap(
                value.toDouble(),
                startTime = startTime,
                endTime = endTime.plusMillis(1),
                startZoneOffset = null,
                endZoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [ActivityEvent] records. */
    private suspend fun writeActivityEvent(value: String, startTime: Instant,endTime : Instant): Boolean {
        // TODO: eventType: https://developer.android.com/reference/kotlin/androidx/health/connect/client/records/ActivityEvent.EventType
        val records = listOf(
            ActivityEvent(
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
    private suspend fun writeActiveCaloriesBurned(value: String, startTime: Instant,endTime : Instant): Boolean {
        checkParseDoubleValue(value)
        val records = listOf(
            ActiveCaloriesBurned(
                value.toDouble(),
                startTime = startTime,
                endTime = endTime.plusMillis(1),
                startZoneOffset = null,
                endZoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    /** * Writes in [ActiveEnergyBurned] records. */
    private suspend fun writeActiveEnergyBurned(value: String, startTime: Instant,endTime : Instant): Boolean {

        checkParseDoubleValue(value)
        val records = listOf(
            ActiveEnergyBurned(
                value.toDouble(),
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
            Weight(
                value.toDouble(),
                time = startTime,
                zoneOffset = null,
                metadata = Metadata(UUID.randomUUID().toString())
            )
        )
        return healthConnectClient.insertRecords(records).recordUidsList.isNotEmpty()
    }

    private fun checkParseDoubleValue(value :String){
        if(value.toDoubleOrNull() == null){
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
