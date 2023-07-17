package com.example.noteapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.databinding.ReminderItemLayoutBinding
import com.example.noteapp.fragments.RemindersFragmentDirections
import com.example.noteapp.model.Reminder
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.commonmark.node.SoftLineBreak

class ReminderAdapter: ListAdapter<Reminder, ReminderAdapter.ReminderViewHolder>(DiffUtilCallBackReminder()) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.reminder_item_layout,parent,false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        getItem(position).let { reminder ->
            holder.apply {
                parent.transitionName = "reminder_${reminder.Id}"
                title.text = reminder.title
                markwon.setMarkdown(content,reminder.content)
                parent.setCardBackgroundColor(reminder.color)
                time.text = " ${reminder.date}, ${reminder.time}"

                parent.setOnClickListener{
                    val action = RemindersFragmentDirections.
                    actionRemindersFragmentToSaveOrUpdateReminderFragment().setReminder(reminder)

                    val extras = FragmentNavigatorExtras(parent to "reminder_${reminder.Id}")

                    Navigation.findNavController(it).navigate(action,extras)
                }


            }
        }
    }

}

