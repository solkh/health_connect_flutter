import 'dart:convert';

import 'package:health_connect_flutter/models/record_type_enum.dart';

class RecordModel {
  double? value;
  String? date;
  String? unit;
  RecordTypeEnum? recordType;
  RecordModel({
    this.value,
    this.date,
    this.unit,
    this.recordType,
  });

  RecordModel copyWith({
    double? value,
    String? date,
    String? unit,
    RecordTypeEnum? recordType,
  }) {
    return RecordModel(
      value: value ?? this.value,
      date: date ?? this.date,
      unit: unit ?? this.unit,
      recordType: recordType ?? this.recordType,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'value': value,
      'date': date,
      'unit': unit,
      'recordType': recordType?.index,
    };
  }

  factory RecordModel.fromMap(Map<String, dynamic> map) {
    return RecordModel(
      value: map['value']?.toDouble(),
      date: map['date'],
      unit: map['unit'],
      recordType: map['recordType'] != null ? RecordTypeEnum.values[map['recordType']] : null,
    );
  }

  String toJson() => json.encode(toMap());

  factory RecordModel.fromJson(String source) => RecordModel.fromMap(json.decode(source));

  @override
  String toString() {
    return 'RecordModel(value: $value, date: $date, unit: $unit, recordType: $recordType)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is RecordModel && other.value == value && other.date == date && other.unit == unit && other.recordType == recordType;
  }

  @override
  int get hashCode {
    return value.hashCode ^ date.hashCode ^ unit.hashCode ^ recordType.hashCode;
  }
}
