import 'dart:developer';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:health_connect_flutter/health_connect_flutter.dart';
import 'package:health_connect_flutter/models/permission_type_enum.dart';
import 'package:health_connect_flutter/models/record_model.dart';
import 'package:health_connect_flutter/models/record_type_enum.dart';
import 'package:health_connect_flutter/models/record_unit.dart';

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
  final RecordTypeEnum mainRecordType = RecordTypeEnum.STEPS;

  @override
  void initState() {
    super.initState();
  }

  Future<void> loadWeightRecords() async {
    try {
      recodeList = await _healthConnectFlutterPlugin.readRecords(types: [mainRecordType], startTime: DateTime(1990).toIso8601String());
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
            _getTotalSteps(),
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
          var recordTypeList = [mainRecordType];
          var result = await _healthConnectFlutterPlugin.requestPermissions(
            permissionType: List.generate(recordTypeList.length, (index) => PermissionTypeEnum.READ_WRITE),
            recordType: recordTypeList,
          );

          log(result.toString());
        } catch (err) {
          log(err.toString());
        }
      },
    );
  }

  Widget _getTotalSteps() {
    return Row(
      children: [
        ElevatedButton(
          child: const Text(" get Total Steps"),
          onPressed: () async {
            try {
              var result = await _healthConnectFlutterPlugin.getTotalSteps(startTime: DateTime(1990).toIso8601String());
              log(result.toString());
            } catch (err) {
              log(err.toString());
            }
          },
        ),
      ],
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
                      await _healthConnectFlutterPlugin.writeRecords(value.toString(), mainRecordType, startTime: DateTime.now().toIso8601String());

                  if (!mounted) return;
                  if (result) {
                    log('Write $mainRecordType successfully');
                    loadWeightRecords();
                  } else {
                    log('Write $mainRecordType Faild');
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
              title: Text('${item.value} ${item.unit?.value}'),
              trailing: Text(item.startTime ?? ''),
            ),
          );
        },
      ),
    );
  }
}
