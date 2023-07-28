package com.example.noteapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.databinding.NoteItemLayoutBinding
import com.example.noteapp.databinding.ReminderItemLayoutBinding
import com.example.noteapp.fragments.NoteFragmentDirections
import com.example.noteapp.fragments.RemindersFragmentDirections
import com.example.noteapp.fragments.SearchFragmentDirections
import com.example.noteapp.model.Note
import com.example.noteapp.model.Reminder
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.commonmark.node.SoftLineBreak
import java.text.SimpleDateFormat
import java.util.Calendar

class SearchRecAdapter(private var list: MutableList<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("SimpleDateFormat")
    private val simpleTimeFormat = SimpleDateFormat("HH:mm")
    @SuppressLint("SimpleDateFormat")
    private val simpleDateTimeFormat = SimpleDateFormat("MMMM dd, HH:mm")
    override fun getItemViewType(position: Int): Int {
        val item = list[position]
        return if (item is Reminder) 1 else 0
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = NoteItemLayoutBinding.bind(itemView)
        val title: MaterialTextView = binding.noteItemTitle
        val content: TextView = binding.noteContent
        val parent: MaterialCardView = binding.noteItemParent
        val markwon = Markwon.builder(itemView.context).usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(itemView.context))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(SoftLineBreak::class.java) { visitor, _ ->
                        visitor.forceNewLine()

                    }
                }
            }).build()
    }
    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ReminderItemLayoutBinding.bind(itemView)
        val title = binding.remindItemTitle
        val content:TextView = binding.remindContent
        val time = binding.remindTime
        val parent = binding.remindItemParent
        val markwon = Markwon.builder(itemView.context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(itemView.context))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(SoftLineBreak::class.java) { visitor, _ ->
                        visitor.forceNewLine()
                    }
                }
            }).build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType ==0) {
            NoteViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.note_item_layout, parent, false)
            )
        }else{
            ReminderViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.reminder_item_layout,parent,false))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if(item is Note && holder is NoteViewHolder) {
            holder.apply {
                parent.transitionName = "recyclerView_${item.Id}"
                title.text = item.title
                markwon.setMarkdown(content, item.content)
                parent.setCardBackgroundColor(item.color)

                itemView.setOnClickListener {
                    val action =
                        SearchFragmentDirections.actionSearchFragmentToSaveOrUpdateFragment()
                            .setNote(item)

                    val extras = FragmentNavigatorExtras(parent to "recyclerView_${item.Id}")

                    Navigation.findNavController(it).navigate(action, extras)
                }

                content.setOnClickListener {
                    val action =
                        SearchFragmentDirections.actionSearchFragmentToSaveOrUpdateFragment()
                            .setNote(item)
                    val extras = FragmentNavigatorExtras(parent to "recyclerView_${position}")
                    Navigation.findNavController(it).navigate(action, extras)
                }

                title.setOnClickListener {
                    val action =
                        SearchFragmentDirections.actionSearchFragmentToSaveOrUpdateFragment()
                            .setNote(item)
                    val extras = FragmentNavigatorExtras(parent to "recyclerView_${position}")
                    Navigation.findNavController(it).navigate(action, extras)
                }
            }
        }else if(item is Reminder && holder is ReminderViewHolder){
            holder.apply {
                parent.transitionName = "reminder_${item.Id}"
                title.text = item.title
                markwon.setMarkdown(content, item.content)
                parent.setCardBackgroundColor(item.color)

                val calendarRemind = Calendar.getInstance()
                calendarRemind.timeInMillis = item.time
                val calendarToday = Calendar.getInstance()
                if (checkDate(calendarRemind, calendarToday) == "Today") {
                    time.text = "Today, ${simpleTimeFormat.format(calendarRemind.time)}"
                } else if (checkDate(calendarRemind, calendarToday) == "Tomorrow") {
                    time.text = "Tomorrow, ${simpleTimeFormat.format(calendarRemind.time)}"
                } else time.text = simpleDateTimeFormat.format(calendarRemind)

                parent.setOnClickListener{
                    val action = RemindersFragmentDirections.
                    actionRemindersFragmentToSaveOrUpdateReminderFragment().setReminder(item)

                    val extras = FragmentNavigatorExtras(parent to "reminder_${item.Id}")

                    Navigation.findNavController(it).navigate(action,extras)
                }


            }
        }
    }

    fun updateList(notes: List<Any>){
        list.addAll(notes)
        notifyDataSetChanged()
    }

    fun clearList(){
        list.clear()
        notifyDataSetChanged()
    }
    private fun checkDate(calendarRemind: Calendar, calendarToday:Calendar):String{
        if (calendarRemind.get(Calendar.YEAR)==calendarToday.get(Calendar.YEAR)){
            if(calendarRemind.get(Calendar.MONTH)==calendarToday.get(Calendar.MONTH)){
                if(calendarRemind.get(Calendar.DAY_OF_MONTH)==calendarToday.get(Calendar.DAY_OF_MONTH))
                    return "Today"
                else if(calendarRemind.get(Calendar.DAY_OF_MONTH)==calendarToday.get(Calendar.DAY_OF_MONTH)+1)
                    return "Tomorrow"
            }
        }
        return "Else"
    }
}