package com.scgts.sctrace.ad_hoc_action.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import org.threeten.bp.ZonedDateTime

class DatePickerDialogFragment(val setDate: (ZonedDateTime) -> Unit) : DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val now = ZonedDateTime.now()

        return DatePickerDialog(
            requireContext(),
            this,
            now.year,
            now.monthValue - 1, // NOTE: Date picker zero indexed, threeten isn't
            now.dayOfMonth
        )
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val newDate = ZonedDateTime.now()
            .withYear(year)
            .withMonth(month + 1) // NOTE: Date picker zero indexed, threeten isn't
            .withDayOfMonth(dayOfMonth)
        setDate(newDate)
    }
}