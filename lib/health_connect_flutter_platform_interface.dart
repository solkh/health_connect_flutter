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

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
