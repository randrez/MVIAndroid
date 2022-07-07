package util

import android.content.Context
import android.text.format.DateUtils
import java.util.*

// Formats a date for display like December 28, 2020
fun Calendar.formatDateTime(context: Context) = DateUtils.formatDateTime(context, timeInMillis, DateUtils.FORMAT_SHOW_YEAR)