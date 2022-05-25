import 'package:flutter/material.dart';
import 'package:health_connect_flutter/health_connect_flutter_method_channel.dart';

import 'health_connect_flutter_platform_interface.dart';

class HealthConnectFlutter {
  Future<String?> getPlatformVersion() async {
    try {
      String? versionName = await HealthConnectFlutterPlatform.instance.getPlatformVersionName();
      int? versionCode = await HealthConnectFlutterPlatform.instance.getPlatformVersionCode();

      return '${versionName ?? ''} + ${versionCode ?? ''}';
    } catch (err) {
      debugPrint(err.toString());
      rethrow;
    }
  }

  Future<bool> requestAuthorization() async {
    try {
      bool result = await MethodChannelHealthConnectFlutter().requestAuthorization() ?? false;
      return result;
    } catch (err) {
      rethrow;
    }
  }
}
