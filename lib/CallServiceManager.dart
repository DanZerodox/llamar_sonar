import 'package:flutter/services.dart';

class CallServiceManager {
  static const platform =
      MethodChannel('com.example.llamada_sonar/call_service');

  // Método para iniciar el servicio de llamadas
  Future<void> startCallService() async {
    try {
      await platform.invokeMethod('startCallService');
    } on PlatformException catch (e) {
      print("Error: ${e.message}");
    }
  }

  // Verificar si el permiso para modificar la política de notificaciones está concedido
  Future<bool> checkNotificationPolicyPermission() async {
    try {
      final bool isGranted =
          await platform.invokeMethod('checkNotificationPolicyPermission');
      return isGranted;
    } on PlatformException catch (e) {
      print("Error: ${e.message}");
      return false;
    }
  }

  // Solicitar el permiso para modificar la política de notificaciones
  Future<void> requestNotificationPolicyPermission() async {
    try {
      await platform.invokeMethod('requestNotificationPolicyPermission');
    } on PlatformException catch (e) {
      print("Error: ${e.message}");
    }
  }
}
