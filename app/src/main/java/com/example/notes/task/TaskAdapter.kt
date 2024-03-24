package com.example.notes.task

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(private val tasks: List<TaskDataClass>): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase: FirebaseDatabase
    private var databaseReference: DatabaseReference
    var onItemClickListener: ((TaskDataClass) -> Unit)? = null

    init {
        val currentUser = firebaseAuth.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Users").child(currentUser?.uid.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_row, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.description.text = task.description
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        holder.timeStamp.text = formatter.format(task.timeStamp)

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
                        // edit task
                        val intent = Intent(holder.moreButton.context, AddTask::class.java)
                        intent.putExtra("task", task)
                        intent.putExtra("edit", true)
                        startActivity(holder.moreButton.context, intent, null)
                    }
                    "Delete" -> {
                        // Delete task
                        databaseReference.child("Tasks").child(task.timeStamp.toString()).removeValue()
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