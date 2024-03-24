package com.example.notes.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R

class TaskAdapter(private val tasks: List<TaskDataClass>): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val onItemClickListener: ((TaskDataClass) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_row, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.description.text = task.description
        holder.timeStamp.text = task.timeStamp.toString()

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(task)
        }
        holder.moreButton.setOnClickListener {
            // Show a popup menu
            val popupMenu = PopupMenu(holder.moreButton.context, holder.moreButton)
            // add edit and delete feature in popup menu and add click listener
            popupMenu.menu.add("Edit")
            popupMenu.menu.add("Delete")
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "Edit" -> {
                        // Open edit task screen
                    }
                    "Delete" -> {
                        // Delete task
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_task_title)
        val description: TextView = itemView.findViewById(R.id.tv_task_description)
        val timeStamp: TextView = itemView.findViewById(R.id.tv_task_timeStamp)
        val moreButton: View = itemView.findViewById(R.id.iv_more)
    }
}