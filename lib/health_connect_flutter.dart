import 'package:flutter/material.dart';
import 'package:health_connect_flutter/health_connect_flutter_method_channel.dart';
import 'package:health_connect_flutter/models/permission_type_enum.dart';
import 'package:health_connect_flutter/models/record_model.dart';
import 'package:health_connect_flutter/models/record_type_enum.dart';

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

  Future<bool?> checkHealthConnectAvailability() async {
    return await MethodChannelHealthConnectFlutter().checkHealthConnectAvailability();
  }

  Future<bool> requestPermissions({required List<PermissionTypeEnum> permissionType, required List<RecordTypeEnum> recordType}) async {
    try {
      bool? result = await MethodChannelHealthConnectFlutter().requestPermissions(permissionType, recordType);
      return result ?? false;
    } catch (err) {
      debugPrint(err.toString());
    }
    return false;
  }

  Future<List<RecordModel>> readRecords({required List<RecordTypeEnum> types, String? startTime, String? endTime}) async {
    if (startTime == null) {
      var now = DateTime.now();
      startTime ??= DateTime(now.year, now.month, now.day).toIso8601String();
    }
    endTime ??= DateTime.now().toIso8601String();
    try {
      List? result = await MethodChannelHealthConnectFlutter().readRecords(types, startTime, endTime);
      return result?.map((e) => RecordModel.fromMap(Map.castFrom(e))).toList() ?? [];
    } catch (err) {
      debugPrint(err.toString());
    }
    return [];
  }

  Future<bool> writeRecords(String value, RecordTypeEnum recordType, {String? startTime, String? endTime}) async {
    try {
      if (startTime == null) {
        var now = DateTime.now();
        startTime ??= DateTime(now.year, now.month, now.day).toIso8601String();
      }
      endTime ??= DateTime.now().toIso8601String();
      bool? result = await MethodChannelHealthConnectFlutter().writeRecords(value, recordType, startTime, endTime);
      return result ?? false;
    } catch (err) {
      debugPrint(err.toString());
    }
    return false;
  }

  Future<int> getTotalSteps({String? startTime, String? endTime}) async {
    try {
      if (startTime == null) {
        var now = DateTime.now();
        startTime ??= DateTime(now.year, now.month, now.day).toIso8601String();
      }
      endTime ??= DateTime.now().toIso8601String();
      int? result = await MethodChannelHealthConnectFlutter().getTotalSteps(startTime, endTime);
      return result ?? 0;
    } catch (err) {
      debugPrint(err.toString());
    }
    return 0;
  }

  Future<int> getTotalActivitySession({String? startTime, String? endTime}) async {
    try {
      if (startTime == null) {
        var now = DateTime.now();
        startTime ??= DateTime(now.year, now.month, now.day).toIso8601String();
      }
      endTime ??= DateTime.now().toIso8601String();
      int? result = await MethodChannelHealthConnectFlutter().getTotalActivitySession(startTime, endTime);
      return result ?? 0;
    } catch (err) {
      debugPrint(err.toString());
    }
    return 0;
  }
}
