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
import java.text.SimpleDateFormat
import java.util.Calendar

class ReminderAdapter :
    ListAdapter<Reminder, ReminderAdapter.ReminderViewHolder>(DiffUtilCallBackReminder()) {
    @SuppressLint("SimpleDateFormat")
    private val simpleTimeFormat = SimpleDateFormat("HH:mm")
    @SuppressLint("SimpleDateFormat")
    private val simpleDateTimeFormat = SimpleDateFormat("MMMM dd, HH:mm")

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ReminderItemLayoutBinding.bind(itemView)
        val title = binding.remindItemTitle
        val content: TextView = binding.remindContent
        val time = binding.remindTime
        val parent = binding.remindItemParent
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.reminder_item_layout, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        getItem(position).let { reminder ->
            holder.apply {
                parent.transitionName = "reminder_${reminder.Id}"
                title.text = reminder.title
                markwon.setMarkdown(content, reminder.content)
                parent.setCardBackgroundColor(reminder.color)

                val calendarRemind = Calendar.getInstance()
                calendarRemind.timeInMillis = reminder.time
                val calendarToday = Calendar.getInstance()
                if (checkDate(calendarRemind, calendarToday) == "Today") {
                    time.text = "Today, ${simpleTimeFormat.format(calendarRemind.time)}"
                } else if (checkDate(calendarRemind, calendarToday) == "Tomorrow") {
                    time.text = "Tomorrow, ${simpleTimeFormat.format(calendarRemind.time)}"
                } else time.text = simpleDateTimeFormat.format(calendarRemind)



                parent.setOnClickListener {
                    val action =
                        RemindersFragmentDirections.actionRemindersFragmentToSaveOrUpdateReminderFragment()
                            .setReminder(reminder)

                    val extras = FragmentNavigatorExtras(parent to "reminder_${reminder.Id}")

                    Navigation.findNavController(it).navigate(action, extras)
                }
            }
        }
    }

    private fun checkDate(calendarRemind: Calendar, calendarToday: Calendar): String {
        if (calendarRemind.get(Calendar.YEAR) == calendarToday.get(Calendar.YEAR)) {
            if (calendarRemind.get(Calendar.MONTH) == calendarToday.get(Calendar.MONTH)) {
                if (calendarRemind.get(Calendar.DAY_OF_MONTH) == calendarToday.get(Calendar.DAY_OF_MONTH))
                    return "Today"
                else if (calendarRemind.get(Calendar.DAY_OF_MONTH) == calendarToday.get(Calendar.DAY_OF_MONTH) + 1)
                    return "Tomorrow"
            }
        }
        return "Else"
    }
}

