import 'dart:convert';

import 'package:health_connect_flutter/models/record_type_enum.dart';
import 'package:health_connect_flutter/models/record_unit.dart';

class RecordModel {
  String? value;
  String? startTime;
  String? endTime;
  RecordUnitEnum? unit;
  RecordTypeEnum? recordType;
  String? metadata;
  RecordModel({
    this.value,
    this.startTime,
    this.endTime,
    this.unit,
    this.recordType,
    this.metadata,
  });

  RecordModel copyWith({
    String? value,
    String? startTime,
    String? endTime,
    RecordUnitEnum? unit,
    RecordTypeEnum? recordType,
    String? metadata,
  }) {
    return RecordModel(
      value: value ?? this.value,
      startTime: startTime ?? this.startTime,
      endTime: endTime ?? this.endTime,
      unit: unit ?? this.unit,
      recordType: recordType ?? this.recordType,
      metadata: metadata ?? this.metadata,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'value': value,
      'startTime': startTime,
      'endTime': endTime,
      'unit': unit?.index,
      'recordType': recordType?.index,
      'metadata': metadata,
    };
  }

  factory RecordModel.fromMap(Map<String, dynamic> map) {
    return RecordModel(
      value: map['value'],
      startTime: map['startTime'],
      endTime: map['endTime'],
      unit: map['unit'] != null ? RecordUnitEnum.values[map['unit']] : null,
      recordType: map['recordType'] != null ? RecordTypeEnum.values[map['recordType']] : null,
      metadata: map['metadata'],
    );
  }

  String toJson() => json.encode(toMap());

  factory RecordModel.fromJson(String source) => RecordModel.fromMap(json.decode(source));

  @override
  String toString() {
    return 'RecordModel(value: $value, startTime: $startTime, endTime: $endTime, unit: $unit, recordType: $recordType, metadata: $metadata)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is RecordModel &&
        other.value == value &&
        other.startTime == startTime &&
        other.endTime == endTime &&
        other.unit == unit &&
        other.recordType == recordType &&
        other.metadata == metadata;
  }

  @override
  int get hashCode {
    return value.hashCode ^ startTime.hashCode ^ endTime.hashCode ^ unit.hashCode ^ recordType.hashCode ^ metadata.hashCode;
  }
}
