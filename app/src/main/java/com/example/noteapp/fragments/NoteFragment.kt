package com.example.noteapp.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.R
import com.example.noteapp.activities.MainActivity
import com.example.noteapp.adapter.NoteRecAdapter
import com.example.noteapp.databinding.FragmentNoteBinding
import com.example.noteapp.utils.SwipeToDelete
import com.example.noteapp.utils.hideKeyboard
import com.example.noteapp.viewModel.NoteActivityViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.concurrent.TimeUnit


class NoteFragment : Fragment(R.layout.fragment_note) {

    private lateinit var noteBinding: FragmentNoteBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private lateinit var navController : NavController
    private lateinit var noteAdapter: NoteRecAdapter

    private enum class NavState{
        Opened, Closed
    }
    private var navState = NavState.Closed
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false).apply {
            duration = 350
        }
        enterTransition = MaterialElevationScale(true).apply {
            duration = 350
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteBinding = FragmentNoteBinding.bind(view)
        val activity = activity as MainActivity
        navController = Navigation.findNavController(view)
        requireView().hideKeyboard()
        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            activity.window.statusBarColor = Color.WHITE
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.statusBarColor = Color.parseColor("#9E9D9D")
        }
        initRec()
        setOnClick()
        swipeToDelete(noteBinding.recNote)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDelete= object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val note = noteAdapter.currentList[position]
                noteActivityViewModel.deleteNote(note)
                val snackbar = Snackbar.make(
                    requireView(),"Deleted", Snackbar.LENGTH_LONG
                ).addCallback(object :BaseTransientBottomBar.BaseCallback<Snackbar>(){
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                    }

                    override fun onShown(transientBottomBar: Snackbar?) {
                        transientBottomBar?.setAction("Undo"){
                            noteActivityViewModel.saveNote(note)
                            noteBinding.noData.isVisible = false
                        }

                        super.onShown(transientBottomBar)
                    }
                }).apply {
                    animationMode  = Snackbar.ANIMATION_MODE_FADE
                    setAnchorView(R.id.add_note_fab)
                }

                snackbar.setActionTextColor(
                    ContextCompat.getColor(
                        requireContext(),R.color.yellowOrange
                    )
                )
                snackbar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDelete)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setOnClick(){
        noteBinding.addNoteFab.setOnClickListener {
            noteBinding.appBar.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveOrUpdateFragment())
        }

        noteBinding.innerFab.setOnClickListener {
            noteBinding.appBar.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveOrUpdateFragment())
        }

        noteBinding.tvSearch.setOnClickListener {
            noteBinding.appBar.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSearchFragment())
        }

        // hide text when scroll rec
        noteBinding.recNote.setOnScrollChangeListener{_,scrollX,scrollY,_,oldScroll->
            when{
                scrollY>oldScroll->{
                    noteBinding.fabText.isVisible = false
                }

                scrollX == scrollY ->{
                    noteBinding.fabText.isVisible = true
                }
                else ->{
                    noteBinding.fabText.isVisible = true
                }
            }

        }
        //open Nav
        noteBinding.menu.setOnClickListener {
            if(navState==NavState.Closed) {
                val animator = ObjectAnimator.ofFloat(noteBinding.navView, "translationX", 0f)
                animator.duration = 400
                animator.start()
                noteBinding.coverView.visibility= View.VISIBLE
                navState = NavState.Opened
                noteBinding.navView.menu.getItem(0).isChecked = true
            }else{
                return@setOnClickListener
            }
        }
        //close Nav
        noteBinding.coverView.setOnClickListener {
            closeNav()
        }

        noteBinding.navView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.note ->{
                    closeNav()
                }

                R.id.reminder ->{
                    closeNav()
                    navController.navigate(NoteFragmentDirections.actionNoteFragmentToRemindersFragment())
                }

                R.id.setting ->{
                    closeNav()
                    navController.navigate(NoteFragmentDirections.actionNoteFragmentToSettingFragment())
                }
            }
            true
        }
    }

    private fun initRec(){
        when(resources.configuration.orientation){
            Configuration.ORIENTATION_LANDSCAPE -> displayRec(3)
            Configuration.ORIENTATION_PORTRAIT -> displayRec(2)
        }
    }

    private fun displayRec(spanCount: Int) {
        noteBinding.recNote.apply {
            layoutManager = StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            noteAdapter = NoteRecAdapter()
            noteAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = noteAdapter
            postponeEnterTransition(300L,TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        observerDataChange()
    }

    private fun observerDataChange(){
        noteActivityViewModel.getAllNote().observe(viewLifecycleOwner){list ->
            noteBinding.noData.isVisible = list.isEmpty()
            noteAdapter.submitList(list)
        }
    }
    private fun closeNav(){
        if(navState==NavState.Opened){
            val animator = ObjectAnimator.ofFloat(noteBinding.navView, "translationX", -660f)
            animator.duration = 400
            animator.start()
            navState = NavState.Closed
            animator.addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    noteBinding.coverView.visibility = View.GONE
                }
            })
        }
    }


}