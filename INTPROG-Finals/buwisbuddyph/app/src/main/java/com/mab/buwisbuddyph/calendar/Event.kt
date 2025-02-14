package com.mab.buwisbuddyph.calendar

import java.time.LocalDate
import java.time.LocalTime

data class Event(var name: String, var date: LocalDate, var time: LocalTime) {

    companion object {
        val eventsList = ArrayList<Event>()

        fun eventsForDate(date: LocalDate): ArrayList<Event> {
            val events = ArrayList<Event>()
            for (event in eventsList) {
                if (event.date == date) {
                    events.add(event)
                }
            }
            return events
        }
    }
}
