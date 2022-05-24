
import 'health_connect_flutter_platform_interface.dart';

class HealthConnectFlutter {
  Future<String?> getPlatformVersion() {
    return HealthConnectFlutterPlatform.instance.getPlatformVersion();
  }
}
