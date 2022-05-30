import 'package:flutter/material.dart';
import 'package:health_connect_flutter/health_connect_flutter_method_channel.dart';
import 'package:health_connect_flutter/models/record_model.dart';

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
      bool? result = await MethodChannelHealthConnectFlutter().requestAuthorization();
      return result ?? false;
    } catch (err) {
      debugPrint(err.toString());
    }
    return false;
  }

  Future<List<RecordModel>> readRecords({required List<String> types, String? startDate, String? endDate}) async {
    if (startDate == null) {
      var now = DateTime.now();
      startDate ??= DateTime(now.year, now.month, now.day).toIso8601String();
    }
    endDate ??= DateTime.now().toIso8601String();
    try {
      List? result = await MethodChannelHealthConnectFlutter().readRecords(types, startDate, endDate);
      return result?.map((e) => RecordModel.fromMap(Map.castFrom(e))).toList() ?? [];
    } catch (err) {
      debugPrint(err.toString());
    }
    return [];
  }

  Future<bool> writeRecords(double value, String type, String date) async {
    try {
      bool? result = await MethodChannelHealthConnectFlutter().writeRecords(value, type, date);
      return result ?? false;
    } catch (err) {
      debugPrint(err.toString());
    }
    return false;
  }
}
