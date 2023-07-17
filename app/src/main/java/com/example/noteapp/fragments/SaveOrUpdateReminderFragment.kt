package com.example.noteapp.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.noteapp.R
import com.example.noteapp.databinding.DialogPickTimeBinding
import com.example.noteapp.databinding.FragmentSaveOrUpdateReminderBinding
import com.example.noteapp.model.Reminder
import com.example.noteapp.utils.hideKeyboard
import com.example.noteapp.viewModel.NoteActivityViewModel
import com.google.android.material.transition.MaterialContainerTransform
import java.text.SimpleDateFormat
import java.util.Calendar


class SaveOrUpdateReminderFragment : Fragment(R.layout.fragment_save_or_update_reminder) {

    private var _binding : FragmentSaveOrUpdateReminderBinding?  = null
    private val binding : FragmentSaveOrUpdateReminderBinding
        get() = _binding!!

    private lateinit var navController: NavController
    private var reminder : Reminder? = null
    private val noteActivityViewModel : NoteActivityViewModel by activityViewModels()
    private val arg : SaveOrUpdateReminderFragmentArgs by navArgs()
    private lateinit var result : String

    private lateinit var dialogBinding: DialogPickTimeBinding
    private lateinit var dateTimePickDialog : AlertDialog

    private var alarmHour = -1
    private var alarmMinute = -1

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("MMMM dd")
    @SuppressLint("SimpleDateFormat")
    private val simpleTimeFormat = SimpleDateFormat("HH:mm")

    private var currentPosDate = -1

    private val itemSpinnerDate = arrayOf("Today","Tomorrow","Pick a date...")
    private val itemSpinnerTime = arrayOf("18:00","20:00","Pick a time...")

    private var dateSpinnerAdapter : ArrayAdapter<String>? = null
    private var timeSpinnerAdapter : ArrayAdapter<String>? = null
    private var calendar:Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

       _binding = FragmentSaveOrUpdateReminderBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        dialogBinding = DialogPickTimeBinding.inflate(LayoutInflater.from(context))
        setOnClick()
        dialogSetOnClick()
    }

    private fun setOnClick(){
        binding.backBtn.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }

        binding.saveNote.setOnClickListener {
            saveOrUpdateReminder()
        }

        binding.timePicker.setOnClickListener {
            val builder : AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setCancelable(true)

            if(dateSpinnerAdapter == null) {
                dateSpinnerAdapter = ArrayAdapter(
                    requireContext(), R.layout.spinner_date_time_item, itemSpinnerDate
                )
                dateSpinnerAdapter?.setDropDownViewResource(R.layout.spinner_date_time_item)
            }
            dialogBinding.spinnerDate.adapter = dateSpinnerAdapter
            dialogBinding.spinnerDate.dropDownHorizontalOffset = -300
            if(timeSpinnerAdapter == null) {
                timeSpinnerAdapter = ArrayAdapter(
                    requireContext(), R.layout.spinner_date_time_item, itemSpinnerTime
                )
                timeSpinnerAdapter?.setDropDownViewResource(R.layout.spinner_date_time_item)
            }

            dialogBinding.spinnerTime.adapter = timeSpinnerAdapter

            dialogBinding.pickDate.setOnClickListener {
                dialogBinding.spinnerDate.performClick()
            }

            dialogBinding.pickTime.setOnClickListener {
                dialogBinding.spinnerTime.performClick()
            }
            builder.setView(dialogBinding.root)
            dateTimePickDialog = builder.create()
            dateTimePickDialog.show()
            dateTimePickDialog.window?.setBackgroundDrawableResource(R.drawable.bg_alert_dialog)
            dialogSetOnClick()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val anim = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            scrimColor = Color.TRANSPARENT
            duration = 300L

        }
        sharedElementEnterTransition = anim
        sharedElementReturnTransition = anim
    }

    private fun saveOrUpdateReminder(){
        val title = binding.edtTitle.text.toString()
        val content = binding.edtContent.text.toString()
        if(title.isEmpty() || content.isEmpty()){
            //TODO: show dialog warning
        }else{
            reminder = arg.reminder
            when(reminder){
                null -> {
                    //TODO: Save new Reminder
                    saveReminder(
                        Reminder(0,
                    title,content,simpleDateFormat.format(calendar.time),-1,simpleTimeFormat.format(calendar.time))
                    )
                    result = "Reminder Saved"
                    setFragmentResult(
                        "key", bundleOf("bundleKey" to result)
                    )
                    navController.navigate(SaveOrUpdateReminderFragmentDirections
                        .actionSaveOrUpdateReminderFragmentToRemindersFragment())
                }
                else ->{
                    //TODO: Update Reminder
                    updateReminder()
                    navController.popBackStack()
                }
            }
        }
    }

    private fun saveReminder(reminder: Reminder){
        noteActivityViewModel.saveReminder(reminder,requireContext(),calendar)
    }

    private fun updateReminder(){
        //TODO: Not Implement yet
    }

    private fun dialogSetOnClick(){
        dialogBinding.spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(currentPosDate==position){
                    onItemSelected(parent,view,0,id)
                }
                else {
                    currentPosDate = position
                    when (parent?.getItemAtPosition(position).toString()) {
                        "Today" -> {
                            dialogBinding.dateVal.text = "Today"
                        }

                        "Tomorrow" -> {
                            calendar.add(Calendar.DATE, 1)
                            dialogBinding.dateVal.text = "Tomorrow"
                        }

                        "Pick a date..." -> {
                            showDatePicker()
                        }
                    }
                }
                Log.d("select",parent?.getItemAtPosition(position).toString() )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }

        }

        dialogBinding.spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(parent?.getItemAtPosition(position).toString()){
                    "18:00" ->{
                        calendar.set(Calendar.HOUR_OF_DAY,18)
                        dialogBinding.timeVal.text = "18:00"
                    }

                    "20:00" ->{
                        calendar.set(Calendar.HOUR_OF_DAY,20)
                        dialogBinding.timeVal.text = "20:00"
                    }

                    else ->{
                        showTimePicker()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }

        }
    }

    private fun showTimePicker() {
        val calendarNow = Calendar.getInstance()
        val hourNow = calendarNow.get(Calendar.HOUR_OF_DAY)
        val minNow = calendarNow.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(requireContext(),
            { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
                calendar.set(Calendar.MINUTE,minute)
                dialogBinding.timeVal.text = "$hourOfDay:$minute"
            },hourNow,minNow,true)
        timePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePicker(){
        val calendarNow = Calendar.getInstance()
        val yearNow = calendarNow.get(Calendar.YEAR)
        val monthNow = calendarNow.get(Calendar.MONTH)
        Log.d("monthNow", monthNow.toString())
        val dateNow = calendarNow.get(Calendar.DAY_OF_MONTH)

        // Create a date picker dialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { view: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
               if (yearNow==year && monthNow == month  && dateNow == dayOfMonth){
                   dialogBinding.dateVal.text = "Today"
               }else if(yearNow==year && monthNow== month  && dateNow == dayOfMonth -1){
                   calendar.add(Calendar.DATE,1)
                   dialogBinding.dateVal.text = "Tomorrow"
               }else{
                   calendar.set(year,month,dayOfMonth)
                   dialogBinding.dateVal.text = simpleDateFormat.format(calendar.time)
               }
            },
            yearNow,
            monthNow,
            dateNow
        )
        datePickerDialog.show()
    }

}