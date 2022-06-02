import 'package:health_connect_flutter/models/permission_type_enum.dart';
import 'package:health_connect_flutter/models/record_type_enum.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'health_connect_flutter_method_channel.dart';

abstract class HealthConnectFlutterPlatform extends PlatformInterface {
  /// Constructs a HealthConnectFlutterPlatform.
  HealthConnectFlutterPlatform() : super(token: _token);

  static final Object _token = Object();

  static HealthConnectFlutterPlatform _instance = MethodChannelHealthConnectFlutter();

  /// The default instance of [HealthConnectFlutterPlatform] to use.
  ///
  /// Defaults to [MethodChannelHealthConnectFlutter].
  static HealthConnectFlutterPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [HealthConnectFlutterPlatform] when
  /// they register themselves.
  static set instance(HealthConnectFlutterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersionName();
  Future<int?> getPlatformVersionCode();

  Future<bool?> requestPermissions(List<PermissionTypeEnum> permissionType, List<RecordTypeEnum> recordType);
  Future<bool?> checkHealthConnectAvailability();
  Future<List?> readRecords(List<RecordTypeEnum> recordTypes, String startTime, String endTime);
  Future<bool?> writeRecords(String value, RecordTypeEnum recordType, String startTime, String endTime);
}
