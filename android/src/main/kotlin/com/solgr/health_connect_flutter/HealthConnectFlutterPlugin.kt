package com.solgr.health_connect_flutter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
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


    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegister.registerGeneratedPlugins(flutterEngine)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "health_connect_flutter")
        channel.setMethodCallHandler(this)
        mContext = flutterPluginBinding.applicationContext
        healthConnectManager = HealthConnectManager(mContext)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        lifecycleScope.launch {
            when (call.method) {
                getPlatformVersionName -> result.success(android.os.Build.VERSION.RELEASE)
                getPlatformVersionCode -> result.success(android.os.Build.VERSION.SDK_INT)
                checkHealthConnectAvailability -> checkHealthConnectAvailability(result)
                requestPermissions -> requestPermissionsFunction(call, result)
                readRecords -> readRecords(call, result)
                writeRecords -> writeRecords(call, result)
                getTotalSteps -> getTotalSteps(call,result)
                getTotalActivitySession -> getTotalActivitySession(call,result)
                else -> result.notImplemented()
            }
        }
    }


    private suspend fun getTotalSteps(@NonNull call: MethodCall, @NonNull result: Result) {
        try {
            val isoStartTime = call.argument<String>("startTime")
            val isoEndTime = call.argument<String>("endTime")

            val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            val startDateInstant =
                if (isoStartTime == null) startOfDay.toInstant() else LocalDateTime.parse(
                    isoStartTime
                )
                    .atZone(ZoneId.systemDefault()).toInstant()
            val endDateInstant =
                if (isoEndTime == null) Instant.now() else LocalDateTime.parse(isoEndTime)
                    .atZone(ZoneId.systemDefault()).toInstant()

            if (!healthConnectManager.hasAllPermissions(
                    PermissionHelper().permissionsParser(
                        mutableListOf(PermissionTypeEnum.READ.ordinal),
                        listOf(RecordTypeEnum.STEPS.ordinal)
                    )
                )
            ) {
                result.error(
                    "401",
                    "SecurityException",
                    "request read Record with unpermitted access"
                )
                return
            }

            val steps = healthConnectManager.getTotalSteps( startDateInstant,endDateInstant)
            result.success(steps)

        } catch (err: Exception) {
            result.error("500", err.localizedMessage, "")
        }

    }

    private suspend fun getTotalActivitySession(@NonNull call: MethodCall, @NonNull result: Result) {
        try {
            val isoStartTime = call.argument<String>("startTime")
            val isoEndTime = call.argument<String>("endTime")

            val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            val startDateInstant =
                if (isoStartTime == null) startOfDay.toInstant() else LocalDateTime.parse(
                    isoStartTime
                )
                    .atZone(ZoneId.systemDefault()).toInstant()
            val endDateInstant =
                if (isoEndTime == null) Instant.now() else LocalDateTime.parse(isoEndTime)
                    .atZone(ZoneId.systemDefault()).toInstant()

            if (!healthConnectManager.hasAllPermissions(
                    PermissionHelper().permissionsParser(
                        mutableListOf(PermissionTypeEnum.READ.ordinal),
                        listOf(RecordTypeEnum.ACTIVITY_SESSION.ordinal)
                    )
                )
            ) {
                result.error(
                    "401",
                    "SecurityException",
                    "request read Record with unpermitted access"
                )
                return
            }
            val total = healthConnectManager.getTotalActivitySession( startDateInstant,endDateInstant)
            result.success(total)
        } catch (err: Exception) {
            result.error("500", err.localizedMessage, "")
        }

    }

//
//    suspend fun<T : Record> readRecords2 (request : ReadRecordsRequest2<T>): ReadRecordsResponse<T> {
//        return healthConnectManager.healthConnectClient.readRecords(request)
//    }

    private suspend fun readRecords(@NonNull call: MethodCall, @NonNull result: Result) {
        try {

            var requestParams = call.argument<String>("requestParams")

//            val mapper = jacksonObjectMapper()
//            val obj = Json.decodeFromString<ReadRecordsRequest<Class.forName("Weight") as Class<Record>>>(requestParams)
            //val genres = mapper.readValue<ReadRecordsRequest<Class.forName("Weight") as Class<Weight>>>(requestParams)
            val resultList = emptyList<RecordModel>().toMutableList()
            val recordTypes = call.argument<List<Int>>("recordTypes")
            val isoStartTime = call.argument<String>("startTime")
            val isoEndTime = call.argument<String>("endTime")


            val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            val startDateInstant =
                if (isoStartTime == null) startOfDay.toInstant() else LocalDateTime.parse(
                    isoStartTime
                )
                    .atZone(ZoneId.systemDefault()).toInstant()
            val endDateInstant =
                if (isoEndTime == null) Instant.now() else LocalDateTime.parse(isoEndTime)
                    .atZone(ZoneId.systemDefault()).toInstant()

            if (recordTypes != null) {
                if (!healthConnectManager.hasAllPermissions(
                        PermissionHelper().permissionsParser(
                            mutableListOf(PermissionTypeEnum.READ.ordinal),
                            recordTypes
                        )
                    )
                ) {
                    result.error(
                        "401",
                        "SecurityException",
                        "request read Record with unpermitted access"
                    )
                    return
                }
                for (recordType in recordTypes) {
                    resultList += healthConnectManager.readRecords(
                        RecordTypeEnum.values()[recordType],
                        startDateInstant,
                        endDateInstant
                    )
                }
            }
            result.success(resultList.map { element -> element.toMap() })
        } catch (err: Exception) {
            result.error("500", err.localizedMessage, "")
        }

    }

    private suspend fun writeRecords(@NonNull call: MethodCall, @NonNull result: Result) {
        try {
            val value = call.argument<String>("value")
            val recordType = call.argument<Int>("recordType")
            val isoStartTime = call.argument<String>("startTime")
            val isoEndTime = call.argument<String>("endTime")

            val startTimeInstant =
                if (isoStartTime == null) Instant.now() else LocalDateTime.parse(isoStartTime)
                    .atZone(ZoneId.systemDefault()).toInstant()
            val endTimeInstant =
                if (isoEndTime == null) Instant.now() else LocalDateTime.parse(isoEndTime)
                    .atZone(ZoneId.systemDefault()).toInstant()
            if (value != null && recordType != null) {
                if (!healthConnectManager.hasAllPermissions(
                        PermissionHelper().permissionsParser(
                            mutableListOf(PermissionTypeEnum.WRITE.ordinal),
                            mutableListOf(recordType)
                        )
                    )
                ) {
                    result.error(
                        "401",
                        "SecurityException",
                        "request write ${RecordTypeEnum.values()[recordType]} Record with unpermitted access"
                    )
                    return
                }

                return result.success(
                    healthConnectManager.writeRecords(
                        value,
                        RecordTypeEnum.values()[recordType],
                        startTimeInstant, endTimeInstant,
                    )
                )
            }
            return result.success(false)
        } catch (err: Exception) {
            result.error("500", err.toString(), "")
        }

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

    private suspend fun requestPermissionsFunction(
        @NonNull call: MethodCall,
        @NonNull result: Result
    ) {
        val recordTypes = call.argument<List<Int>>("recordTypes")
        val permissionTypes = call.argument<List<Int>>("permissionTypes")
        val permissionsList = PermissionHelper().permissionsParser(permissionTypes!!, recordTypes!!)
        Log.e(TAG, "permissionTypes: ${permissionTypes.toString()}", )
        Log.e(TAG, "recordTypes: ${recordTypes.toString()}", )
        Log.e(TAG, "permissionsList: ${permissionsList.toString()}", )
        val permissionsGranted = healthConnectManager.hasAllPermissions(permissionsList)
        if (!permissionsGranted) {
            mResult = result
            // request permissions
            val intent = Intent(mActivity, AskPermissionActivity::class.java)
            intent.putExtra("recordTypes", recordTypes.toIntArray())
            intent.putExtra("permissionTypes", permissionTypes.toIntArray())
            mActivity.startActivityForResult(intent, permissionRequestCode)
            //result.error("401", "permissions not granted", "request permissions needed")
        } else {
            return result.success(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && permissionRequestCode == requestCode) {
            var permissionResult = data?.getBooleanExtra(PermissionResult, false)
            if (permissionResult == null) permissionResult = false
            mResult!!.success(permissionResult)
        }
        mResult!!.success(false)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        mActivity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
    }


}
