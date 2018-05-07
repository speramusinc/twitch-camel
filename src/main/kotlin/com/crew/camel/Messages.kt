package com.crew.camel

import java.util.Date

enum class EventType {
    CREATE,
    UPDATE,
    DELETE
}

data class Schedule(val id: String, val start: Date, val end: Date)