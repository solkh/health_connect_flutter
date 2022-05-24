import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'health_connect_flutter_platform_interface.dart';

/// An implementation of [HealthConnectFlutterPlatform] that uses method channels.
class MethodChannelHealthConnectFlutter extends HealthConnectFlutterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('health_connect_flutter');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}