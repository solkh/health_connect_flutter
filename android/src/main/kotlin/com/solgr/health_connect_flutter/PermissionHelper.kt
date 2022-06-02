package com.solgr.health_connect_flutter

import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.*
import com.solgr.health_connect_flutter.models.PermissionTypeEnum
import com.solgr.health_connect_flutter.models.RecordTypeEnum

class PermissionHelper {
    private var permissionList = emptySet<Permission>()

    fun permissionsParser(permissionTypes: List<Int>, recordTypes: List<Int>): Set<Permission> {
        permissionList = emptySet()
        for (permissionType in permissionTypes) {
            when (permissionType) {
                PermissionTypeEnum.READ.ordinal -> readPermissionParser(recordTypes)
                PermissionTypeEnum.WRITE.ordinal -> writePermissionParser(recordTypes)
                PermissionTypeEnum.READ_WRITE.ordinal -> {
                    readPermissionParser(recordTypes)
                    writePermissionParser(recordTypes)
                }

            }
        }
        return permissionList
    }

    private fun readPermissionParser(recordTypes: List<Int>) {
        for (type in recordTypes) {
            permissionList += Permission.createReadPermission(
                when(type){
                    RecordTypeEnum.WEIGHT.ordinal-> Weight::class
                    RecordTypeEnum.ACTIVE_ENERGY_BURNED.ordinal-> ActiveEnergyBurned::class
                    RecordTypeEnum.ACTIVE_CALORIES_BURNED.ordinal-> ActiveCaloriesBurned::class
                    RecordTypeEnum.ACTIVITY_EVENT.ordinal-> ActivityEvent::class
                    RecordTypeEnum.ACTIVITY_LAP.ordinal-> ActivityLap::class
                    RecordTypeEnum.ACTIVITY_SESSION.ordinal-> ActivitySession::class
                    RecordTypeEnum.BASAL_BODY_TEMPERATURE.ordinal-> BasalBodyTemperature::class
                    RecordTypeEnum.BLOOD_GLUCOSE.ordinal-> BloodGlucose::class
                    RecordTypeEnum.BLOOD_PRESSURE.ordinal-> BloodPressure::class
                    RecordTypeEnum.BODY_FAT.ordinal-> BodyFat::class
                    RecordTypeEnum.HEART_RATE.ordinal-> HeartRateSeries::class
                    RecordTypeEnum.HEIGHT.ordinal-> Height::class
                    RecordTypeEnum.STEPS.ordinal-> Steps::class
                    RecordTypeEnum.HYDRATION.ordinal-> Hydration::class
                    else -> throw Exception("ERROR Parsing permissionType")
                }
            )
//            when (type) {
//                RecordTypeEnum.ACTIVE_ENERGY_BURNED.ordinal -> permissionList += listOf(
//                    Permission.createReadPermission(ActiveEnergyBurned::class)
//                )
//                RecordTypeEnum.WEIGHT.ordinal -> permissionList += Permission.createReadPermission(
//                    Weight::class
//                )
//            }
        }
    }


    private fun writePermissionParser(recordTypes: List<Int>) {
        for (type in recordTypes) {
            permissionList +=  Permission.createWritePermission(
                when(type){
                    RecordTypeEnum.WEIGHT.ordinal-> Weight::class
                    RecordTypeEnum.ACTIVE_ENERGY_BURNED.ordinal-> ActiveEnergyBurned::class
                    RecordTypeEnum.ACTIVE_CALORIES_BURNED.ordinal-> ActiveCaloriesBurned::class
                    RecordTypeEnum.ACTIVITY_EVENT.ordinal-> ActivityEvent::class
                    RecordTypeEnum.ACTIVITY_LAP.ordinal-> ActivityLap::class
                    RecordTypeEnum.ACTIVITY_SESSION.ordinal-> ActivitySession::class
                    RecordTypeEnum.BASAL_BODY_TEMPERATURE.ordinal-> BasalBodyTemperature::class
                    RecordTypeEnum.BLOOD_GLUCOSE.ordinal-> BloodGlucose::class
                    RecordTypeEnum.BLOOD_PRESSURE.ordinal-> BloodPressure::class
                    RecordTypeEnum.BODY_FAT.ordinal-> BodyFat::class
                    RecordTypeEnum.HEART_RATE.ordinal-> HeartRateSeries::class
                    RecordTypeEnum.HEIGHT.ordinal-> Height::class
                    RecordTypeEnum.STEPS.ordinal-> Steps::class
                    RecordTypeEnum.HYDRATION.ordinal-> Hydration::class
                    else -> throw Exception("ERROR Parsing permissionType")
                }
            )

        }
    }

}