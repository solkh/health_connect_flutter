package com.solgr.health_connect_flutter.models

enum class RecordTypeEnum {
    /// value :double, startTime
    WEIGHT,

    /// value :double, startTime,endTime
    ACTIVE_CALORIES_BURNED,

    /// value :double, startTime,endTime
    ACTIVE_ENERGY_BURNED,

    /// value :String eventType , startTime, endTime
    ACTIVITY_EVENT,

    /// value :Double,startTime,endTime
    ACTIVITY_LAP,

    /// value :String activityType ,startTime,endTime
    ACTIVITY_SESSION,

    /// value :double,startTime
    BASAL_BODY_TEMPERATURE,

    /// value :Double,startTime
    BLOOD_GLUCOSE,

    /// value :double,startTime
    BLOOD_PRESSURE,

    /// value :Int,startTime
    BODY_FAT,

    /// value :Double ,startTime,endTime
    HEART_RATE,

    /// value :double,startTime
    HEIGHT,

    /// value :Long ,startTime,endTime
    STEPS,

    /// value :Double,startTime,endTime
    HYDRATION,
}

