package com.example.android.politicalpreparedness.network.jsonadapter

import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class ElectionAdapter {
    @FromJson
    fun divisionFromJson(ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter1 = "state:"
        val stateDelimiter2 = "district:"
        var state = ""

        val country = ocdDivisionId.substringAfter(countryDelimiter, "")
            .substringBefore("/")

        if (ocdDivisionId.contains(stateDelimiter1)) {
            state = ocdDivisionId.substringAfter(stateDelimiter1, "")
                .substringBefore("/")
        } else {
            state = ocdDivisionId.substringAfter(stateDelimiter2, "")
                .substringBefore("/")
        }

        return Division(ocdDivisionId, country, state)
    }

    @FromJson
    fun dateFromJson(value: String): Date {
        val localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    @ToJson
    fun dateToJson(date: Date): String {
        return date.toString()
    }

    @ToJson
    fun divisionToJson(division: Division): String {
        return division.id
    }
}