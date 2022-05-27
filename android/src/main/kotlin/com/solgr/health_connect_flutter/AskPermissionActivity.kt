package com.solgr.health_connect_flutter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.permission.HealthDataRequestPermissions
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.Weight


const val PermissionResult = "PermissionResult"

class AskPermissionActivity : AppCompatActivity() {

    private val permissions = setOf(
        Permission.createReadPermission(Weight::class),
        Permission.createWritePermission(Weight::class),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions.launch(permissions)
    }

    // Create the permissions launcher.
    private val requestPermissions =
        registerForActivityResult(HealthDataRequestPermissions()) { granted ->
            if (granted.containsAll(permissions)) {
                // Permissions successfully granted
                Log.i(TAG, "requestPermissions : Permissions successfully granted ")
                returnToFlutterView(true)
            } else {
                // Lack of required permissions
                Log.e(TAG, "requestPermissions : Permissions is required ")
                returnToFlutterView(false)
            }
        }

    private fun returnToFlutterView(result: Boolean) {
        val returnIntent = Intent()
        returnIntent.putExtra(PermissionResult, result)
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}