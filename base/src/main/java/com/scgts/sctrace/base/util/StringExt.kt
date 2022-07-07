package com.scgts.sctrace.base.util

import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

fun String?.toFormattedDate(): String? = this?.let { date ->
    val newDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
    if (newDate != null) {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(newDate)
    } else date
}

fun String.removeProjectString(): String = this.replace("project", "", true).trim()

fun String?.toZonedDateTime(): ZonedDateTime? {
    return this?.let {
        try {
            DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(it, ZonedDateTime::from)
        } catch (e: Exception) {
            null
        }
    }
}