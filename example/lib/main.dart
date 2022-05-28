import 'dart:developer';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:health_connect_flutter/health_connect_flutter.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _healthConnectFlutterPlugin = HealthConnectFlutter();
  final TextEditingController _weightTextEditController = TextEditingController();

  @override
  void initState() {
    super.initState();
    loadWeightRecords();
  }

  Future<void> loadWeightRecords() async {
    try {
      var res = await _healthConnectFlutterPlugin.readRecords(types: ["WEIGHT"]);
      for (var element in res) {
        log(element.toString());
      }
    } catch (err) {
      log(err.toString());
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: ListView(
          children: [
            const SizedBox(height: 24),
            _writeWeight(),
            ElevatedButton(
              child: const Text('requestAuthorization'),
              onPressed: () async {},
            ),
          ],
        ),
      ),
    );
  }

  Widget _writeWeight() {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Row(
        children: [
          const Text('enter Weight Value :'),
          Expanded(
            child: TextField(
              controller: _weightTextEditController,
              keyboardType: const TextInputType.numberWithOptions(signed: true),
            ),
          ),
          ElevatedButton(
            child: const Text('Save'),
            onPressed: () async {
              try {
                var value = double.tryParse(_weightTextEditController.text);
                if (value != null) {
                  var result = await _healthConnectFlutterPlugin.writeRecords(value, 'Weight', DateTime.now().toIso8601String());

                  if (!mounted) return;
                  if (result) {
                    log('Write Weight successfully');
                    loadWeightRecords();
                  } else {
                    log('Write Weight Faild');
                  }
                }
              } catch (err) {
                log(err.toString());
              }

              if (!mounted) return;
            },
          ),
        ],
      ),
    );
  }
}
