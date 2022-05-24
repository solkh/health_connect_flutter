package com.solgr.health_connect_flutter

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import com.solgr.health_connect_flutter.models.*
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

const val TAG = "Health Connect Plugin"

/** HealthConnectFlutterPlugin */
class HealthConnectFlutterPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context: Context

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Log.i(TAG, "onAttachedToEngine: onAttachedToEngine")
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "health_connect_flutter")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext;
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      getPlatformVersionName -> result.success(android.os.Build.VERSION.RELEASE)
      getPlatformVersionCode -> result.success(android.os.Build.VERSION.SDK_INT)
      else ->   result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
