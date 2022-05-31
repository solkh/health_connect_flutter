import 'dart:convert';

import 'package:health_connect_flutter/models/record_type_enum.dart';
import 'package:health_connect_flutter/models/record_unit.dart';

class RecordModel {
  double? value;
  String? startDate;
  String? endDate;
  RecordUnitEnum? unit;
  RecordTypeEnum? recordType;
  RecordModel({
    this.value,
    this.startDate,
    this.endDate,
    this.unit,
    this.recordType,
  });

  RecordModel copyWith({
    double? value,
    String? startDate,
    String? endDate,
    RecordUnitEnum? unit,
    RecordTypeEnum? recordType,
  }) {
    return RecordModel(
      value: value ?? this.value,
      startDate: startDate ?? this.startDate,
      endDate: endDate ?? this.endDate,
      unit: unit ?? this.unit,
      recordType: recordType ?? this.recordType,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'value': value,
      'startDate': startDate,
      'endDate': endDate,
      'unit': unit?.index,
      'recordType': recordType?.index,
    };
  }

  factory RecordModel.fromMap(Map<String, dynamic> map) {
    return RecordModel(
      value: map['value']?.toDouble(),
      startDate: map['startDate'],
      endDate: map['endDate'],
      unit: map['unit'] != null ? RecordUnitEnum.values[map['unit']] : null,
      recordType: map['recordType'] != null ? RecordTypeEnum.values[map['recordType']] : null,
    );
  }

  String toJson() => json.encode(toMap());

  factory RecordModel.fromJson(String source) => RecordModel.fromMap(json.decode(source));

  @override
  String toString() {
    return 'RecordModel(value: $value, startDate: $startDate, endDate: $endDate, unit: $unit, recordType: $recordType)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is RecordModel &&
        other.value == value &&
        other.startDate == startDate &&
        other.endDate == endDate &&
        other.unit == unit &&
        other.recordType == recordType;
  }

  @override
  int get hashCode {
    return value.hashCode ^ startDate.hashCode ^ endDate.hashCode ^ unit.hashCode ^ recordType.hashCode;
  }
}
