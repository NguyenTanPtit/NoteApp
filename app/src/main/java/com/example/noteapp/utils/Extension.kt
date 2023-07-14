package com.example.noteapp.utils

import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import java.text.SimpleDateFormat

private val timeFormat by lazy { SimpleDateFormat("hh:mm") }
fun View.hideKeyboard() = (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
    .hideSoftInputFromWindow(windowToken,HIDE_NOT_ALWAYS)

