package com.example.noteapp.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.noteapp.adapter.ReminderAdapter
import com.example.noteapp.databinding.FragmentRemindersBinding
import com.example.noteapp.utils.SwipeToDelete
import com.example.noteapp.viewModel.NoteActivityViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit


class RemindersFragment : Fragment(R.layout.fragment_reminders) {

    private var _binding : FragmentRemindersBinding? = null
    private val binding: FragmentRemindersBinding
        get() = _binding!!
    private lateinit var navController: NavController

    private lateinit var remindAdapter: ReminderAdapter

    private val noteViewModel : NoteActivityViewModel by activityViewModels()
    @SuppressLint("SimpleDateFormat")
    private val simpleDateTimeFormat = SimpleDateFormat("dd/MM/yyyy, HH:mm")

    private enum class NavState{
        Opened, Closed
    }
    private var navState = NavState.Closed
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentRemindersBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        initRec()
        setOnClick()
        swipeToDelete(binding.recReminder)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false).apply {
            duration = 350
        }
        enterTransition = MaterialElevationScale(true).apply {
            duration = 350
        }

    }

    private fun setOnClick(){
        binding.menu.setOnClickListener {
            if(navState== NavState.Closed) {
                val animator = ObjectAnimator.ofFloat(binding.navView, "translationX", 0f)
                animator.duration = 300
                animator.start()
                binding.coverView.visibility= View.VISIBLE
                navState = NavState.Opened
                binding.navView.menu.getItem(1).isChecked = true
            }else{
                return@setOnClickListener
            }
        }

        binding.coverView.setOnClickListener{
            closeNav()
        }

        binding.navView.setNavigationItemSelectedListener {menuItem ->
            closeNav()
            when(menuItem.itemId){
                R.id.note -> navController.navigate(RemindersFragmentDirections
                    .actionRemindersFragmentToNoteFragment())

                R.id.reminder -> return@setNavigationItemSelectedListener true

                R.id.setting -> navController.navigate(RemindersFragmentDirections
                    .actionRemindersFragmentToSettingFragment())
            }
            true
        }

        binding.innerFab.setOnClickListener {
            navController.navigate(RemindersFragmentDirections
                .actionRemindersFragmentToSaveOrUpdateReminderFragment())
        }

        binding.tvSearch.setOnClickListener {
            navController.navigate(RemindersFragmentDirections.actionRemindersFragmentToSearchFragment())
        }

    }

    private fun closeNav(){
        if(navState== NavState.Opened){
            val animator = ObjectAnimator.ofFloat(binding.navView, "translationX", -660f)
            animator.duration = 300
            animator.start()
            navState = NavState.Closed
            animator.addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    binding.coverView.visibility = View.GONE
                }
            })
        }
    }

    private fun initRec(){
        when(resources.configuration.orientation){
            Configuration.ORIENTATION_LANDSCAPE -> displayRec(3)
            Configuration.ORIENTATION_PORTRAIT -> displayRec(2)
        }
    }

    private fun displayRec(spanCount:Int){
        binding.recReminder.apply {
            layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            remindAdapter = ReminderAdapter()
            adapter = remindAdapter
            postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        observerDataChange()
    }

    private fun observerDataChange(){
        noteViewModel.getAllRemind().observe(viewLifecycleOwner){ list->
            remindAdapter.submitList(list)
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDelete= object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val reminder = remindAdapter.currentList[position]
                val calendarRemind = Calendar.getInstance()
                val dateTimeRemind = simpleDateTimeFormat.parse(reminder.time)
                if(dateTimeRemind!=null) {
                    calendarRemind.time = dateTimeRemind
                }
                noteViewModel.deleteReminder(reminder)
                val snackbar = Snackbar.make(
                    requireView(),"Deleted", Snackbar.LENGTH_LONG
                ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>(){
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                    }

                    override fun onShown(transientBottomBar: Snackbar?) {
                        transientBottomBar?.setAction("Undo"){
                            noteViewModel.saveReminder(reminder,requireContext(),calendarRemind)
                        }

                        super.onShown(transientBottomBar)
                    }
                }).apply {
                    animationMode  = Snackbar.ANIMATION_MODE_FADE
                    setAnchorView(R.id.add_reminder_fab)
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

}