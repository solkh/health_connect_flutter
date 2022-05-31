import 'dart:developer';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:health_connect_flutter/health_connect_flutter.dart';
import 'package:health_connect_flutter/models/permission_type_enum.dart';
import 'package:health_connect_flutter/models/record_model.dart';
import 'package:health_connect_flutter/models/record_type_enum.dart';

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
  List<RecordModel> recodeList = [];

  @override
  void initState() {
    super.initState();
  }

  Future<void> loadWeightRecords() async {
    try {
      recodeList =
          await _healthConnectFlutterPlugin.readRecords(types: [RecordTypeEnum.ACTIVE_ENERGY_BURNED], startDate: DateTime(1990).toIso8601String());
      setState(() {});
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
        body: Column(
          children: [
            const SizedBox(height: 24),
            _requistPermission(),
            _writeData(),
            _recordList(),
          ],
        ),
      ),
    );
  }

  Widget _requistPermission() {
    return ElevatedButton(
      child: const Text("Request Permissions"),
      onPressed: () async {
        try {
          var result = await _healthConnectFlutterPlugin.requestPermissions(
            permissionType: [PermissionTypeEnum.READ_WRITE],
            recordType: [RecordTypeEnum.ACTIVE_ENERGY_BURNED],
          );

          log(result.toString());
        } catch (err) {
          log(err.toString());
        }
      },
    );
  }

  Widget _writeData() {
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
                  var result =
                      await _healthConnectFlutterPlugin.writeRecords(value, RecordTypeEnum.ACTIVE_ENERGY_BURNED, DateTime.now().toIso8601String());

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

  Widget _recordList() {
    return Expanded(
      child: ListView.builder(
        itemCount: recodeList.length,
        itemBuilder: (context, index) {
          RecordModel item = recodeList[index];
          return Card(
            child: ListTile(
              title: Text('${item.value} ${item.unit}'),
              trailing: Text(item.startDate ?? ''),
            ),
          );
        },
      ),
    );
  }
}
