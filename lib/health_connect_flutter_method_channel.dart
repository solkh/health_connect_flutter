import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'health_connect_flutter_platform_interface.dart';

/// An implementation of [HealthConnectFlutterPlatform] that uses method channels.
class MethodChannelHealthConnectFlutter extends HealthConnectFlutterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('health_connect_flutter');

  @override
  Future<String?> getPlatformVersionName() async {
    return await methodChannel.invokeMethod<String>('getPlatformVersionName');
  }

  @override
  Future<int?> getPlatformVersionCode() async {
    return await methodChannel.invokeMethod<int>('getPlatformVersionCode');
  }

  @override
  Future<bool?> requestAuthorization() async {
    return await methodChannel.invokeMethod<bool>('requestAuthorization');
  }

  @override
  Future<bool?> checkHealthConnectAvailability() async {
    return await methodChannel.invokeMethod<bool>('checkHealthConnectAvailability');
  }

  @override
  Future<List?> readRecords(List<String> types, String startDate, String endDate) async {
    Map<String, dynamic> arguments = {
      "types": types,
      "startDate": startDate,
      "endDate": endDate,
    };
    return await methodChannel.invokeMethod<List?>('readRecords', arguments);
  }

  @override
  Future<bool?> writeRecords(double value, String type, String date) async {
    Map<String, dynamic> arguments = {
      "value": value,
      "type": type,
      "date": date,
    };
    return await methodChannel.invokeMethod<bool>('writeRecords', arguments);
  }
}
