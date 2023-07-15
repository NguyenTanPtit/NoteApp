package com.example.noteapp.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.R
import com.example.noteapp.adapter.ReminderAdapter
import com.example.noteapp.databinding.FragmentRemindersBinding
import com.example.noteapp.viewModel.NoteActivityViewModel
import com.google.android.material.transition.MaterialElevationScale
import java.util.concurrent.TimeUnit


class RemindersFragment : Fragment(R.layout.fragment_reminders) {

    private var _binding : FragmentRemindersBinding? = null
    private val binding: FragmentRemindersBinding
        get() = _binding!!
    private lateinit var navController: NavController

    private lateinit var remindAdapter: ReminderAdapter

    private val noteViewModel : NoteActivityViewModel by activityViewModels()

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

}