package com.example.islamic.ui.daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.islamic.R
import com.example.islamic.data.model.DailyPractice
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AddEditPracticeDialog(
    private val practice: DailyPractice? = null,
    private val onSave: (name: String, time: String, day:String) -> Unit
) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_edit_practice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinnerDay = view.findViewById<Spinner>(R.id.spinnerDay)
        val etPracticeTime = view.findViewById<TextInputEditText>(R.id.etPracticeTime)

        val dayOptions = resources.getStringArray(R.array.day_options)

        if (practice != null) {
            val position = dayOptions.indexOf(practice.day)
            if (position >= 0) spinnerDay.setSelection(position)
            view.findViewById<TextInputEditText>(R.id.etPracticeName).setText(practice.name)

            val formatter = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val formattedTime = formatter.format(java.util.Date(practice.time))
            etPracticeTime.setText(formattedTime)
        } else {
            val currentDay = getCurrentDayName()
            val index = dayOptions.indexOf(currentDay)
            if (index >= 0) spinnerDay.setSelection(index)
        }

        etPracticeTime.setOnClickListener {
            val calendar = Calendar.getInstance()

            if (practice != null) {
                calendar.timeInMillis = practice.time
            }

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = android.app.TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    val formatted = String.format("%02d:%02d", selectedHour, selectedMinute)
                    etPracticeTime.setText(formatted)
                },
                hour, minute, true
            )
            timePicker.show()
        }

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dismiss()
        }

        view.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = view.findViewById<TextInputEditText>(R.id.etPracticeName).text.toString()
            val time = etPracticeTime.text.toString()
            val day = spinnerDay.selectedItem.toString()

            if (name.isBlank() || time.isBlank()) {
                Toast.makeText(requireContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onSave(name, time, day)
            dismiss()
        }
    }

    private fun getCurrentDayName(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            Calendar.SUNDAY -> "Sunday"
            else -> "Monday" // fallback
        }
    }
}