import 'package:flutter_test/flutter_test.dart';
import 'package:health_connect_flutter/health_connect_flutter.dart';
import 'package:health_connect_flutter/health_connect_flutter_platform_interface.dart';
import 'package:health_connect_flutter/health_connect_flutter_method_channel.dart';
import 'package:health_connect_flutter/models/permission_type_enum.dart';
import 'package:health_connect_flutter/models/record_type_enum.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockHealthConnectFlutterPlatform with MockPlatformInterfaceMixin implements HealthConnectFlutterPlatform {
  @override
  Future<String?> getPlatformVersionName() => Future.value('42');
  @override
  Future<int?> getPlatformVersionCode() => Future.value(42);

  @override
  Future<bool?> requestPermissions(List<PermissionTypeEnum> permissionType, List<RecordTypeEnum> recordType) => Future.value(false);

  @override
  Future<bool?> checkHealthConnectAvailability() => Future.value(false);
  @override
  Future<List<Map<String, dynamic>>?> readRecords(List<RecordTypeEnum> recordTypes, String startDate, String endDate) => Future.value([]);

  @override
  Future<bool?> writeRecords(String value, RecordTypeEnum recordType, String startTime, String endTime) => Future.value(false);

  @override
  Future<int?> getTotalSteps(String startTime, String endTime) => Future.value(0);

  @override
  Future<int?> getTotalActivitySession(String startTime, String endTime) => Future.value(0);
}

void main() {
  final HealthConnectFlutterPlatform initialPlatform = HealthConnectFlutterPlatform.instance;

  test('$MethodChannelHealthConnectFlutter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelHealthConnectFlutter>());
  });

  test('getPlatformVersion', () async {
    HealthConnectFlutter healthConnectFlutterPlugin = HealthConnectFlutter();
    MockHealthConnectFlutterPlatform fakePlatform = MockHealthConnectFlutterPlatform();
    HealthConnectFlutterPlatform.instance = fakePlatform;

    expect(await healthConnectFlutterPlugin.getPlatformVersion(), '42');
  });
}
