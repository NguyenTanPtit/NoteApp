package com.example.noteapp.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.noteapp.db.NoteDB
import com.example.noteapp.model.Note
import com.example.noteapp.model.Reminder
import com.example.noteapp.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class FirebaseRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference
    private lateinit var noteRepo: NoteRepository

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
    fun getAllNoteAndRemind(uid:String):List<Any>{
        val res:MutableList<Any> = mutableListOf()
        database = Firebase.database.reference
        database.child("UsersData").child(uid).child("Notes")
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children){
                        val note = item.getValue(Note::class.java)
                        note?.let {
                            res.add(note)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //TODO("Not yet implemented")
                }

            })
        database.child("UsersData").child(uid).child("Reminders")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children){
                        val reminder = item.getValue(Reminder::class.java)
                        reminder?.let {
                            res.add(it)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //TODO("Not yet implemented")
                }

            })
        return res
    }
    suspend fun syncNoteAndRemind(context: Context){
        val db = NoteDB.invoke(context)
        noteRepo = NoteRepository(db)

    }



    private fun updateData(){
        //TODO update data to firebase
    }

}