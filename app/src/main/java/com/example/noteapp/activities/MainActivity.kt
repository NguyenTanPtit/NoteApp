package com.example.noteapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.noteapp.R
import com.example.noteapp.databinding.ActivityMainBinding
import com.example.noteapp.db.NoteDB
import com.example.noteapp.repository.NoteRepository
import com.example.noteapp.viewModel.NoteActivityViewModel
import com.example.noteapp.viewModel.NoteActivityViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var noteActivityViewModel: NoteActivityViewModel
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val noteRepository = NoteRepository(NoteDB(this))

        val noteActivityViewModelFactory = NoteActivityViewModelFactory(noteRepository)
        noteActivityViewModel = ViewModelProvider(this,
        noteActivityViewModelFactory)[NoteActivityViewModel::class.java]
    }
}