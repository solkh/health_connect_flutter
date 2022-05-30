import 'dart:convert';

class RecordModel {
  double? value;
  String? data;
  String? unit;
  RecordModel({
    this.value,
    this.data,
    this.unit,
  });

  RecordModel copyWith({
    double? value,
    String? data,
    String? unit,
  }) {
    return RecordModel(
      value: value ?? this.value,
      data: data ?? this.data,
      unit: unit ?? this.unit,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'value': value,
      'data': data,
      'unit': unit,
    };
  }

  factory RecordModel.fromMap(Map<String, dynamic> map) {
    return RecordModel(
      value: map['value']?.toDouble(),
      data: map['data'],
      unit: map['unit'],
    );
  }

  String toJson() => json.encode(toMap());

  factory RecordModel.fromJson(String source) => RecordModel.fromMap(json.decode(source));

  @override
  String toString() => 'RecordModel(value: $value, data: $data, unit: $unit)';

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is RecordModel && other.value == value && other.data == data && other.unit == unit;
  }

  @override
  int get hashCode => value.hashCode ^ data.hashCode ^ unit.hashCode;
}
