import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:health_connect_flutter/models/permission_type_enum.dart';
import 'package:health_connect_flutter/models/record_type_enum.dart';

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
  Future<bool?> requestPermissions(List<PermissionTypeEnum> permissionType, List<RecordTypeEnum> recordType) async {
    Map<String, dynamic> arguments = {
      "permissionTypes": permissionType.map((e) => e.index).toList(),
      "recordTypes": recordType.map((e) => e.index).toList(),
    };
    return await methodChannel.invokeMethod<bool>('requestPermissions', arguments);
  }

  @override
  Future<bool?> checkHealthConnectAvailability() async {
    return await methodChannel.invokeMethod<bool>('checkHealthConnectAvailability');
  }

  @override
  Future<List?> readRecords(List<RecordTypeEnum> recordTypes, String startTime, String endTime) async {
    Map<String, dynamic> arguments = {
      "recordTypes": recordTypes.map((e) => e.index).toList(),
      "startTime": startTime,
      "endTime": endTime,
    };
    return await methodChannel.invokeMethod<List?>('readRecords', arguments);
  }

  @override
  Future<bool?> writeRecords(String value, RecordTypeEnum recordType, String startTime, String endTime) async {
    Map<String, dynamic> arguments = {
      "value": value,
      "recordType": recordType.index,
      "startTime": startTime,
      "endTime": endTime,
    };
    return await methodChannel.invokeMethod<bool>('writeRecords', arguments);
  }

  @override
  Future<int?> getTotalSteps(String startTime, String endTime) async {
    Map<String, dynamic> arguments = {
      "startTime": startTime,
      "endTime": endTime,
    };
    return await methodChannel.invokeMethod<int>('getTotalSteps', arguments);
  }
}
