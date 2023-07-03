package com.example.noteapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.databinding.NoteItemLayoutBinding
import com.example.noteapp.model.Note
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.commonmark.node.SoftLineBreak

class SearchRecAdapter(private var list: MutableList<Note>) :
    RecyclerView.Adapter<SearchRecAdapter.ViewHolder>() {



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.note_item_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = list[position]
        holder.apply {
            title.text = note.title
            markwon.setMarkdown(content,note.content)
            parent.setCardBackgroundColor(note.color)

            itemView.setOnClickListener {

            }

            content.setOnClickListener {

            }
        }
    }

    fun updateList(notes: List<Note>){
        list.clear()
        list.addAll(notes)
        notifyDataSetChanged()
    }
}