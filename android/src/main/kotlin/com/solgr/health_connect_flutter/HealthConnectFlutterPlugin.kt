package com.solgr.health_connect_flutter

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.Weight
import com.solgr.health_connect_flutter.models.getPlatformVersionCode
import com.solgr.health_connect_flutter.models.getPlatformVersionName
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

const val TAG = "Health Connect Plugin"

/** HealthConnectFlutterPlugin */
class HealthConnectFlutterPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var healthConnectManager: HealthConnectManager
    private var mResult: Result? = null

    val permissions = setOf(
        Permission.createReadPermission(Weight::class),
        Permission.createWritePermission(Weight::class),
    )


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        Log.i(TAG, "onAttachedToEngine: onAttachedToEngine")
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "health_connect_flutter")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext;
        healthConnectManager = HealthConnectManager(context)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        checkHealthConnectAvailability(result)

        when (call.method) {
            getPlatformVersionName -> result.success(android.os.Build.VERSION.RELEASE)
            getPlatformVersionCode -> result.success(android.os.Build.VERSION.SDK_INT)

//                requestAuthorization -> requestAuthorization(result)
            else -> result.notImplemented()

        }

    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun checkHealthConnectAvailability(@NonNull result: Result) {
        when (healthConnectManager.availability) {
            HealthConnectAvailability.NOT_INSTALLED -> result.error(
                "NOT_INSTALLED",
                "Health Connect APK not installed on device",
                ""
            )
            HealthConnectAvailability.NOT_SUPPORTED -> result.error(
                "NOT_SUPPORTED",
                "Health Connect not supported.api level >= 27",
                ""
            )
            else -> print("Health Connect is available and APK in installed")
        }
    }

    private suspend fun requestAuthorization(@NonNull result: Result): Boolean {
        val permissionsGranted = healthConnectManager.hasAllPermissions(permissions)
        if (!permissionsGranted) {
            mResult = result
            // TODO:  request permissions
            throw Exception("permissions not granted \\n request permissions needed");
        } else {
            return permissionsGranted;
        }
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    override fun onDetachedFromActivityForConfigChanges() {
        TODO("Not yet implemented")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }
}
