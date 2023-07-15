package com.example.noteapp.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentSaveOrUpdateReminderBinding
import com.example.noteapp.model.Reminder
import com.example.noteapp.utils.hideKeyboard
import com.example.noteapp.viewModel.NoteActivityViewModel
import com.google.android.material.transition.MaterialContainerTransform


class SaveOrUpdateReminderFragment : Fragment(R.layout.fragment_save_or_update_reminder) {

    private var _binding : FragmentSaveOrUpdateReminderBinding?  = null
    private val binding : FragmentSaveOrUpdateReminderBinding
        get() = _binding!!

    private lateinit var navController: NavController
    private var reminder : Reminder? = null
    private val noteActivityViewModel : NoteActivityViewModel by activityViewModels()
    private val arg : SaveOrUpdateReminderFragmentArgs by navArgs()

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
        setOnClick()
    }

    private fun setOnClick(){
        binding.backBtn.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }

        binding.saveNote.setOnClickListener {
            saveOrUpdateReminder()
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
                    title,content,"date here",-1,"time here")
                    )
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
        //TODO: Not Implement yet
    }

    private fun updateReminder(){
        //TODO: Not Implement yet
    }

}