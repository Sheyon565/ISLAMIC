package com.example.islamic.ui.daily

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.AdapterView
import kotlinx.coroutines.launch
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.islamic.R
import com.example.islamic.data.model.DailyPractice
import com.example.islamic.data.repository.PracticeRepository
import com.example.islamic.viewmodel.PracticeViewModel
import com.example.islamic.viewmodel.PracticeViewModelFactory
import com.example.islamic.data.PracticeDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class DailyPracticeFragment : Fragment() {

    private lateinit var viewModel: PracticeViewModel
    private lateinit var adapter: DailyPracticeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daily_practice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = PracticeRepository(
            practiceDao = PracticeDatabase.getDatabase(requireContext()).practiceDao()
        )
        val viewModelFactory = PracticeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[PracticeViewModel::class.java]

        adapter = DailyPracticeAdapter(
            onEditClick = { showAddEditDialog(it) },
            onDeleteClick = { practiceItem ->
                practiceItem.id?.let { practiceId ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.deletePractice(practiceId, practiceItem.day)
                    }
                }
            }
        )

        view.findViewById<RecyclerView>(R.id.rvDailyPractices).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DailyPracticeFragment.adapter
        }

        val spinnerDay = view.findViewById<Spinner>(R.id.spinnerDayFilter)
        val dayOptions = resources.getStringArray(R.array.day_options)
        spinnerDay.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dayOptions).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val currentDay = getCurrentDayName()
        val currentIndex = dayOptions.indexOf(currentDay).takeIf { it >= 0 } ?: 0
        spinnerDay.setSelection(currentIndex)
        
        spinnerDay.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view:View?, position: Int, id: Long) {
                val selectedDay = dayOptions[position]
                viewModel.loadPracticesForDay(selectedDay)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        viewModel.practicesForDay.observe(viewLifecycleOwner) { practices ->
            adapter.submitList(practices)
        }

        val selectedDay = spinnerDay.selectedItem?.toString() ?: dayOptions.first()
        viewModel.loadPracticesForDay(selectedDay)

        view.findViewById<FloatingActionButton>(R.id.fabAddPractice).setOnClickListener {
            showAddEditDialog()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showAddEditDialog(practice: DailyPractice? = null) {
        val dialog = AddEditPracticeDialog(
            practice = practice,
            onSave = { name, timeString, day ->
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                val parsedData = formatter.parse(timeString)
                val timeMillis = parsedData?.time ?: System.currentTimeMillis()
                if (currentUserId != null) {
                    if (practice == null) {
                        // Tambah baru
                        val newPractice = DailyPractice(
                            name = name,
                            time = timeMillis,
                            userId = currentUserId,
                            day = day
                        )
                        viewModel.addPractice(newPractice)
                    } else {
                        // Edit
                        val updatedPractice = practice.copy(name = name, time = timeMillis, day = day)
                        viewModel.updatePractice(practice.id!!, updatedPractice)
                    }
                    viewModel.loadPracticesForDay(day)
                    setSpinnerToday(day)
                } else {
                    // Handle not logged-in user
                }
            }
        )
        dialog.show(parentFragmentManager, "AddEditPracticeDialog")
    }

    private fun setSpinnerToday(day: String) {
        val spinnerDay = view?.findViewById<Spinner>(R.id.spinnerDayFilter) ?: return
        val dayOptions = resources.getStringArray(R.array.day_options)
        val index = dayOptions.indexOf(day)
        if (index >= 0) {
            spinnerDay.setSelection(index)
        }
    }

    private fun getCurrentDayName(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tueday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            Calendar.SUNDAY -> "Sunday"
            else -> "Monday"
        }
    }
}
