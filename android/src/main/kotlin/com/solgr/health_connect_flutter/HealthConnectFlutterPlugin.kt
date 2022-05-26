package com.solgr.health_connect_flutter

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.Weight
import androidx.lifecycle.lifecycleScope
import com.solgr.health_connect_flutter.models.getPlatformVersionCode
import com.solgr.health_connect_flutter.models.getPlatformVersionName
import com.solgr.health_connect_flutter.models.requestAuthorization
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

const val TAG = "Health Connect Plugin"

/** HealthConnectFlutterPlugin */
class HealthConnectFlutterPlugin : FlutterPlugin, MethodCallHandler, FlutterActivity() {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
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


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        Log.i(TAG, "%%%%%%%%%%%%%%%%%%%%%%%%%% onAttachedToEngine ")
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "health_connect_flutter")
        channel.setMethodCallHandler(this)
        mContext = flutterPluginBinding.applicationContext
        healthConnectManager = HealthConnectManager(mContext)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        Log.i(TAG, "%%%%%%%%%%%%%%%%%%%%%%%%%% onMethodCall ")
        if (checkHealthConnectAvailability(result)) {
            lifecycleScope.launch {
                when (call.method) {
                    getPlatformVersionName -> result.success(android.os.Build.VERSION.RELEASE)
                    getPlatformVersionCode -> result.success(android.os.Build.VERSION.SDK_INT)
                    requestAuthorization -> requestAuthorizationFunction(result)
                    else -> result.notImplemented()
                }
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun checkHealthConnectAvailability(@NonNull result: Result): Boolean {

        when (HealthConnectavailability) {
            HealthConnectAvailability.NOT_INSTALLED -> result.error(
                "NOT_INSTALLED",
                "Health Connect APK not installed on device",
                ""
            )
            HealthConnectAvailability.NOT_SUPPORTED -> result.error(
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
            // TODO:  request permissions
            result.error("401","permissions not granted" ,"request permissions needed")
        } else {
            return result.success(true);
        }
    }

//    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
//        Log.i(TAG, "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% onAttachedToActivity: ")
//        print("%%%%%%%%%%%%%%%% onAttachedToActivity")
//        mActivity = binding.activity
//    }
//
//    override fun onDetachedFromActivityForConfigChanges() {
//        Log.i(
//            TAG,
//            "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% onDetachedFromActivityForConfigChanges: "
//        )
//        print("%%%%%%%%%%%%%%%% onDetachedFromActivityForConfigChanges")
//    }
//
//    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
//        Log.i(
//            TAG,
//            "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% onReattachedToActivityForConfigChanges: "
//        )
//        print("%%%%%%%%%%%%%%%% onReattachedToActivityForConfigChanges")
//    }
//
//    override fun onDetachedFromActivity() {
//        Log.i(TAG, "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% onDetachedFromActivity: ")
//        print("%%%%%%%%%%%%%%%% onDetachedFromActivity")
//    }
}
