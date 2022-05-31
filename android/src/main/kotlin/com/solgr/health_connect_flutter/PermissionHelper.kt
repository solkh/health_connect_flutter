package com.solgr.health_connect_flutter

import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.ActiveEnergyBurned
import androidx.health.connect.client.records.Weight
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
            when (type) {
                RecordTypeEnum.ACTIVE_ENERGY_BURNED.ordinal -> permissionList += listOf(
                    Permission.createReadPermission(
                        ActiveEnergyBurned::class
                    )
                )
                RecordTypeEnum.Weight.ordinal -> permissionList += Permission.createReadPermission(
                    Weight::class
                )
            }
        }
    }


    private fun writePermissionParser(recordTypes: List<Int>) {
        for (type in recordTypes) {
            when (type) {
                RecordTypeEnum.ACTIVE_ENERGY_BURNED.ordinal -> permissionList += Permission.createWritePermission(
                    ActiveEnergyBurned::class
                )
                RecordTypeEnum.Weight.ordinal -> permissionList += Permission.createWritePermission(
                    Weight::class
                )
            }
        }
    }

}