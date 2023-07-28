package com.example.noteapp.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.noteapp.model.Note
import com.example.noteapp.model.Reminder
import com.example.noteapp.model.User
import com.example.noteapp.viewModel.NoteActivityViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.coroutines.resumeWithException


class FirebaseRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference

    @SuppressLint("SimpleDateFormat")
    private val simpleDateTimeFormat = SimpleDateFormat("dd/MM/yyyy, HH:mm")

    // Sign in using google
    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential): MutableLiveData<ResponseState<User>> {
        val authenticatedUserMutableLiveData: MutableLiveData<ResponseState<User>> =
            MutableLiveData()

        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                val isNewUser = authTask.result?.additionalUserInfo?.isNewUser
                val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val name = firebaseUser.displayName
                    val email = firebaseUser.email
                    val user = User(uid = uid, name = name, email = email)
                    user.isNew = isNewUser
                    authenticatedUserMutableLiveData.value = ResponseState.Success(user)
                }
            } else {
                authenticatedUserMutableLiveData.value = authTask.exception?.message?.let {
                    ResponseState.Error(it)
                }

            }
        }
        return authenticatedUserMutableLiveData
    }

    private suspend fun getAllNoteFirebase(uid: String):MutableMap<Int,Note> {
        return withContext(Dispatchers.IO){
            suspendCancellableCoroutine {continuation ->
                database = Firebase.database.reference
                database. child("UserData").child(uid).child("Notes")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val res: MutableMap<Int, Note> = mutableMapOf()
                            for (item in snapshot.children) {
                                val note = item.getValue(Note::class.java)
                                note?.let {
                                    res[it.Id] = it
                                }
                            }
                            continuation.resume(res,null)
                        }
                        override fun onCancelled(error: DatabaseError) {
                            //TODO("Not yet implemented")
                            continuation.resumeWithException(error.toException())
                        }
                    })
            }

        }

    }

    private suspend fun getAllReminderFirebase(uid: String): MutableMap<Int,Reminder>{
        return withContext(Dispatchers.IO){
            suspendCancellableCoroutine { continuation ->
                val res: MutableMap<Int, Reminder> = mutableMapOf()
                database = Firebase.database.reference
                database.child("UserData").child(uid).child("Reminders")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (item in snapshot.children) {
                                val reminder = item.getValue(Reminder::class.java)
                                reminder?.let {
                                    res[it.Id] = it
                                }
                            }
                            continuation.resume(res,null)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            //TODO("Not yet implemented")
                            continuation.resumeWithException(error.toException())
                        }
                    })
            }
        }
    }

    fun syncNoteAndRemind(uid: String, context: Context, viewModel: NoteActivityViewModel) {
        GlobalScope.launch (Dispatchers.IO){

            val listNoteLocal: List<Note> = viewModel.getAllNoteSync()
            Log.d("listNoteLocal", listNoteLocal.size.toString())
            val listReminderLocal: List<Reminder> = viewModel.getAllRemindSync()
            Log.d("listReminderLocal", listReminderLocal.size.toString())
            val deferredListNoteFirebase =async {
                getAllNoteFirebase(uid)
            }
            val listNoteFirebase = deferredListNoteFirebase.await()
            Log.d("Firebase size", listNoteFirebase.size.toString())
            noteDataSynchronization(listNoteLocal,listNoteFirebase,uid,context, viewModel)

            val deferredListReminderFirebase=async {
                getAllReminderFirebase(uid)
            }
            val listReminderFirebase = deferredListReminderFirebase.await()
            reminderDataSynchronization(listReminderLocal,listReminderFirebase,uid,context, viewModel)
        }


    }

    private fun noteDataSynchronization(
        localList: List<Note>,
        firebaseNotes: Map<Int, Note>,
        uid: String, context: Context, viewModel: NoteActivityViewModel
    ) {
        //update or save new note from local to firebase
        Log.d("firebaseNotes", firebaseNotes.size.toString())
        for (item in localList) {
            val noteFirebase = firebaseNotes[item.Id]
            if (noteFirebase != null) {
                if (item.color != noteFirebase.color || item.content != noteFirebase.content ||
                    item.title != noteFirebase.title || item.date != noteFirebase.date
                ) {
                    updateNoteData(uid, item, context)
                }
            } else {
                updateNoteData(uid, item, context)
            }
        }
        // save note don't have on local
        for (note in firebaseNotes.values) {
            if (note.Id !in localList.map { it.Id }) {
                viewModel.saveNote(note)
            }
        }
    }

    private fun reminderDataSynchronization(
        localList: List<Reminder>,
        firebaseReminder: Map<Int, Reminder>,
        uid: String, context: Context, viewModel: NoteActivityViewModel
    ) {
        //update or save new reminder from local to firebase
        for (item in localList) {
            val reminderFirebase = firebaseReminder[item.Id]
            if (reminderFirebase != null) {
                if (item.color != reminderFirebase.color || item.content != reminderFirebase.content ||
                    item.title != reminderFirebase.title || item.date != reminderFirebase.date ||
                    item.time != reminderFirebase.time
                ) {
                    updateReminderData(uid, item, context)
                }
            } else {
                updateReminderData(uid, item, context)
            }
        }
        // save reminder don't have on local
        for (reminder in firebaseReminder.values) {
            if (reminder.Id !in localList.map { it.Id }) {
                val cal = Calendar.getInstance()
                cal.timeInMillis = reminder.time
                viewModel.saveReminder(reminder, context, cal)
            }
        }
    }

    private fun updateNoteData(uid: String, item: Note, context: Context) {
        database = Firebase.database.reference
        database.child("UserData").child(uid).child("Notes")
            .child("${item.Id}").setValue(item).addOnSuccessListener {
                Toast.makeText(context, "Updated data!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.d("update Note", it.stackTrace.toString())
                Toast.makeText(context, "Fail to update!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateReminderData(uid: String, item: Reminder, context: Context) {
        database = Firebase.database.reference
        database.child("UserData").child(uid).child("Reminders")
            .child("${item.Id}").setValue(item)
            .addOnSuccessListener {
                Toast.makeText(context, "Updated data!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.d("update Note", it.stackTrace.toString())
                Toast.makeText(context, "Fail to update!", Toast.LENGTH_SHORT).show()
            }
    }

}
