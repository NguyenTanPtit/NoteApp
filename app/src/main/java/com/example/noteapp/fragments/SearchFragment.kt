package com.example.noteapp.fragments

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.adapter.NoteRecAdapter
import com.example.noteapp.adapter.SearchRecAdapter
import com.example.noteapp.databinding.FragmentSearchBinding
import com.example.noteapp.model.Note
import com.example.noteapp.utils.hideKeyboard
import com.example.noteapp.viewModel.NoteActivityViewModel
import java.util.concurrent.TimeUnit


class SearchFragment : Fragment() {

    private var _binding : FragmentSearchBinding? = null
    private val binding:FragmentSearchBinding
        get() = _binding!!
    private val noteActivityViewModel:NoteActivityViewModel by activityViewModels()

    private lateinit var searchAdapter:SearchRecAdapter
    private var noteList : MutableList<Note> = mutableListOf()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.edtSearch.requestFocus()

        initView()

        when(resources.configuration.orientation){
            Configuration.ORIENTATION_LANDSCAPE -> displayRec(3)
            Configuration.ORIENTATION_PORTRAIT -> displayRec(2)
        }
    }

    private fun displayRec(spanCount:Int) {
        binding.recSearch.apply {
            layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            searchAdapter = SearchRecAdapter(noteList)
            searchAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = searchAdapter
            postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }

    }

    private fun initView() {

        binding.edtSearch.setOnEditorActionListener{ v,actionId,_ ->
            if(actionId==EditorInfo.IME_ACTION_SEARCH){
                v.clearFocus()
                requireView().hideKeyboard()
            }
            return@setOnEditorActionListener true
        }

        binding.edtSearch.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()){
                    val text = s.toString()
                    val query = "%$text%"
                    if(query.isNotEmpty()){
                        noteActivityViewModel.searchNote(query).observe(viewLifecycleOwner){
                            searchAdapter.updateList(it)
                        }
                    }else{
                        noteList.clear()
                        searchAdapter.updateList(noteList)
                    }
                }else{
                    noteList.clear()
                    searchAdapter.updateList(noteList)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.backBtn.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }
    }



}