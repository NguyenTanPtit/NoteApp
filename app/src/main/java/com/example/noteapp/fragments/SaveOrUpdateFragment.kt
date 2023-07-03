package com.example.noteapp.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.noteapp.R
import com.example.noteapp.databinding.BottomSheetLayoutBinding
import com.example.noteapp.databinding.FragmentSaveOrUpdateBinding
import com.example.noteapp.model.Note
import com.example.noteapp.utils.hideKeyboard
import com.example.noteapp.viewModel.NoteActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date


class SaveOrUpdateFragment : Fragment(R.layout.fragment_save_or_update) {

    private var _binding: FragmentSaveOrUpdateBinding? = null
    private val binding: FragmentSaveOrUpdateBinding
        get() = _binding!!

    private lateinit var navController: NavController
    private var note: Note? = null
    private var color = -1
    private lateinit var result : String
    private val noteViewModel: NoteActivityViewModel by activityViewModels()
    private val currentDate = SimpleDateFormat.getInstance().format(Date())
    private val job = CoroutineScope(Dispatchers.Main)
    private val args: SaveOrUpdateFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaveOrUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        ViewCompat.setTransitionName(binding.noteContentFragmentParent,
            "recyclerView_${args.note?.Id}")
        initView()
        setOnClick()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val note = args.note
        val title = binding.edtTile
        val content =  binding.edtNoteContent
        val lastEdit = binding.lastEdited
        if(note==null){
            binding.lastEdited.text = "Edited on: ${SimpleDateFormat.getDateInstance().format(Date())}"
        }else{
            color = note.color
            title.setText(note.title)
            content.renderMD(note.content)
            lastEdit.text = "Edited on ${note.date}"

            binding.apply {
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(color)
                }
                toolbarFragmentNoteContent.setBackgroundColor(color)
                bottomBar.setBackgroundColor(color)
            }
            activity?.window?.statusBarColor= note.color
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


    private fun setOnClick(){
        binding.backBtn.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }

        binding.saveNote.setOnClickListener {
            saveNote()
        }

        try {
            binding.edtNoteContent.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.bottomBar.visibility = View.VISIBLE
                    binding.edtNoteContent.setStylesBar(binding.styleBar)
                } else
                    binding.bottomBar.visibility = View.GONE
            }
        } catch (e: Throwable) {
            Log.d("hideBar", e.stackTrace.toString())
        }

        binding.fabColorPicker.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(
                requireContext(),R.style.BottomSheetDiaLogTheme
            )

            val bottomSheetView : View = layoutInflater
                .inflate(R.layout.bottom_sheet_layout,null,false)
            val bottomSheetBinding = BottomSheetLayoutBinding.bind(bottomSheetView)
            with(bottomSheetDialog){
                setContentView(bottomSheetView)
                show()
            }
            bottomSheetBinding.colorPicker.apply {
                setSelectedColor(color)
                setOnColorSelectedListener {value->
                    color = value
                    binding.apply {
                        noteContentFragmentParent.setBackgroundColor(color)
                        toolbarFragmentNoteContent.setBackgroundColor(color)
                        bottomBar.setBackgroundColor(color)
                        activity?.window?.statusBarColor  = color
                    }
                    bottomSheetBinding.bottomSheetParent.setCardBackgroundColor(color)
                }
            }
            bottomSheetBinding.bottomSheetParent.setCardBackgroundColor(color)
            bottomSheetView.post {
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
    private fun saveNote() {
        val content = binding.edtNoteContent.text.toString()
        val title = binding.edtTile.text.toString()
        if(content.isEmpty()||title.isEmpty()){
            //TODO: show dialog warning
        }else{
            note = args.note
            when(note){
                null -> {
                    noteViewModel.saveNote(
                        Note(
                            0, title, binding.edtNoteContent.getMD(), currentDate, color
                        )
                    )
                    result = "Note Saved"
                    setFragmentResult(
                        "key", bundleOf("bundleKey" to result)
                    )
                    navController.navigate(
                        SaveOrUpdateFragmentDirections.actionSaveOrUpdateFragmentToNoteFragment()
                    )
                }
                else ->{
                    //TODO: update note
                    updateNote()
                    navController.popBackStack()
                }
            }
        }

    }

    private fun updateNote() {
        if(note != null){
            noteViewModel.updateNote(
                Note(
                    note!!.Id,
                    binding.edtTile.text.toString(),
                    binding.edtNoteContent.getMD(),
                    currentDate,
                    color
                )
            )
        }
    }

}