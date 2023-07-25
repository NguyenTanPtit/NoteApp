package com.example.noteapp.utils

import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat

fun View.hideKeyboard() = (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
    .hideSoftInputFromWindow(windowToken,HIDE_NOT_ALWAYS)

fun ImageView.loadImage(id:Int) = Glide.with(context).load(id).into(this)

