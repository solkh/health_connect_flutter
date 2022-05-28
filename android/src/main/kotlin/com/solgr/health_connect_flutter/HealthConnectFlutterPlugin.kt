package com.solgr.health_connect_flutter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.Weight
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import com.solgr.health_connect_flutter.models.*
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.util.GeneratedPluginRegister
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

const val TAG = "Health Connect Plugin"

/** HealthConnectFlutterPlugin */
class HealthConnectFlutterPlugin : FlutterPlugin, MethodCallHandler, FlutterActivity(),
    ActivityAware {

    private val permissionRequestCode = 401
    private lateinit var channel: MethodChannel
    private lateinit var healthConnectManager: HealthConnectManager
    private lateinit var mContext: Context
    private lateinit var mActivity: Activity
    private var mResult: Result? = null

    private val permissions = setOf(
        Permission.createReadPermission(Weight::class),
        Permission.createWritePermission(Weight::class),
    )

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegister.registerGeneratedPlugins(flutterEngine)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        Log.i(TAG, "%%%%%%%%%%%%%%%%%%%%%%%%%% onAttachedToEngine ")
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "health_connect_flutter")
        channel.setMethodCallHandler(this)
        mContext = flutterPluginBinding.applicationContext
        healthConnectManager = HealthConnectManager(mContext)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        Log.i(TAG, "%%%%%%%%%%%%%%%%%%%%%%%%%% onMethodCall ")
        lifecycleScope.launch {
            when (call.method) {
                getPlatformVersionName -> result.success(android.os.Build.VERSION.RELEASE)
                getPlatformVersionCode -> result.success(android.os.Build.VERSION.SDK_INT)
                checkHealthConnectAvailability -> checkHealthConnectAvailability(result)
                requestAuthorization -> requestAuthorizationFunction(result)
                readRecords -> readRecords(call, result)
                writeRecords -> writeRecords(call, result)
                else -> result.notImplemented()
            }
        }
    }

    private suspend fun readRecords(@NonNull call: MethodCall, @NonNull result: Result) {
        println("call arguments : ${call.arguments}")

        var resultList = emptyList<Map<String, Any>>()
        val readTypes = call.argument<List<String>>("types")
        val startIsoDate = call.argument<String>("startDate")
        val endIsoDate = call.argument<String>("endDate")

        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val startDateInstant =
            if (startIsoDate == null) startOfDay.toInstant() else LocalDateTime.parse(startIsoDate)
                .atZone(ZoneId.systemDefault()).toInstant()
        val endDateInstant =
            if (endIsoDate == null) Instant.now() else LocalDateTime.parse(endIsoDate)
                .atZone(ZoneId.systemDefault()).toInstant()

        if (readTypes != null) {
            for (recordTypeString in readTypes) {
                println(recordTypeString)
                val type = RecodeTypeParser().fromString(recordTypeString)
                println(type)
                val request = ReadRecordsRequest(
                    recordType = Weight::class,
                    timeRangeFilter = TimeRangeFilter.between(startDateInstant, endDateInstant)
                )
                val response = healthConnectManager.healthConnectClient.readRecords(request)
                response.records
                Log.e(TAG, "readRecords: $recordTypeString ${response.records}")
                for (item in response.records) {

                    var res = mapOf("value" to item.weightKg,
                        "date" to item.time.toString(),
                        "unit" to "KG"
                    )
                    resultList += res;
                }
            }
        }
        result.success(resultList)
    }

    private suspend fun writeRecords(@NonNull call: MethodCall, @NonNull result: Result) {
        println("call arguments : ${call.arguments}")
        val value = call.argument<Double>("value")
        val writeType = call.argument<String>("type")
        val isoDate = call.argument<String>("date")

        val dateInstant =
            if (isoDate == null) Instant.now() else LocalDateTime.parse(isoDate)
                .atZone(ZoneId.systemDefault()).toInstant()

        val records = listOf(Weight(value!!, time = dateInstant, zoneOffset = null))
        val recordResponse =
            healthConnectManager.healthConnectClient.insertRecords(records)
        if (recordResponse.recordUidsList.isNotEmpty()) {
            return result.success(true)
        }
        return result.success(false)
    }

    private fun checkHealthConnectAvailability(@NonNull result: Result): Boolean {
        when (HealthConnectAvailability) {
            HealthConnectAvailabilityStatus.NOT_INSTALLED -> result.error(
                "NOT_INSTALLED",
                "Health Connect APK not installed on device",
                ""
            )
            HealthConnectAvailabilityStatus.NOT_SUPPORTED -> result.error(
                "NOT_SUPPORTED",
                "Health Connect not supported.\ncurrent api level ${android.os.Build.VERSION.SDK_INT},required api level >= $MIN_SUPPORTED_SDK",
                ""
            )
            else -> return true
        }
        return false
    }

    private suspend fun requestAuthorizationFunction(@NonNull result: Result) {
        val permissionsGranted = healthConnectManager.hasAllPermissions(permissions)
        if (!permissionsGranted) {
            mResult = result
            // request permissions
            val intent = Intent(mActivity, AskPermissionActivity::class.java)
            mActivity.startActivityForResult(intent, permissionRequestCode)
//            result.error("401", "permissions not granted", "request permissions needed")
        } else {
            return result.success(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            var permissionResult = data?.getBooleanExtra(PermissionResult, false)
            if (permissionResult == null) permissionResult = false
            mResult!!.success(permissionResult)
        }
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        Log.i(TAG, "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% onAttachedToActivity")
        print("%%%%%%%%%%%%%%%% onAttachedToActivity")
        mActivity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        Log.i(TAG, "%%%%%%%%%%%%%%%%%%%%%%%% onDetachedFromActivityForConfigChanges")
        print("%%%%%%%%%%%%%%%% onDetachedFromActivityForConfigChanges")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        Log.i(TAG, "%%%%%%%%%%%%%%%%%%%%%%%% onReattachedToActivityForConfigChanges")
        print("%%%%%%%%%%%%%%%% onReattachedToActivityForConfigChanges")
    }

    override fun onDetachedFromActivity() {
        Log.i(TAG, "%%%%%%%%%%%%%%%%%%%%%%%% onDetachedFromActivity:")
        print("%%%%%%%%%%%%%%%% onDetachedFromActivity")
    }
}
