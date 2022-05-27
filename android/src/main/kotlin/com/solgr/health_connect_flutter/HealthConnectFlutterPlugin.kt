package com.solgr.health_connect_flutter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.Weight
import androidx.lifecycle.lifecycleScope
import com.solgr.health_connect_flutter.models.checkHealthConnectAvailability
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
                else -> result.notImplemented()
            }
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

    private suspend fun requestAuthorizationFunction(@NonNull result: Result) {
        val permissionsGranted = healthConnectManager.hasAllPermissions(permissions)
        if (!permissionsGranted) {
            mResult = result
            // request permissions
            val intent = Intent(mActivity, AskPermissionActivity::class.java)
            mActivity.startActivityForResult(intent, permissionRequestCode)
//            result.error("401", "permissions not granted", "request permissions needed")
        } else {
            return result.success(true);
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
