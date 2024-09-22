import 'package:flutter/material.dart';
import 'CallServiceManager.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  final CallServiceManager _callServiceManager = CallServiceManager();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Llamada Sonar',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final CallServiceManager _callServiceManager = CallServiceManager();

  @override
  void initState() {
    super.initState();
    _checkAndRequestPermissions();
    _startCallService(); // Inicia el servicio al iniciar la app
  }

  Future<void> _checkAndRequestPermissions() async {
    // Verifica si el permiso de "No molestar" está concedido
    bool isGranted =
        await _callServiceManager.checkNotificationPolicyPermission();
    if (!isGranted) {
      // Si no está concedido, solicita el permiso
      await _callServiceManager.requestNotificationPolicyPermission();
    }
  }

  // Método para iniciar el servicio de llamadas automáticamente
  void _startCallService() {
    _callServiceManager.startCallService();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Sonar Llamadas en Modo Silencio'),
      ),
      body: Center(
        child: Text('Servicio de llamadas activado automáticamente'),
      ),
    );
  }
}
