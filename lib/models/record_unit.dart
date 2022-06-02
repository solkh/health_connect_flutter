// ignore_for_file: constant_identifier_names

enum RecordUnitEnum {
  BEATS_PER_MINUTE,
  CALORIES,
  COUNT,
  DEGREE_CELSIUS,
  GRAMS,
  KILOGRAMS,
  METERS,
  MILLIGRAM_PER_DECILITER,
  MILLIMETER_OF_MERCURY,
  MILLISECONDS,
  MINUTES,
  NO_UNIT,
  PERCENTAGE,
  SIEMENS,
  UNKNOWN_UNIT,
  LITER,
}

extension ParseToString on RecordUnitEnum {
  String get value {
    return toString().split('.').last;
  }
}
