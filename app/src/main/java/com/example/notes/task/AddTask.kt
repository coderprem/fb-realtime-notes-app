package com.example.notes.task

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notes.MainActivity
import com.example.notes.R
import com.example.notes.databinding.ActivityAddTaskBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddTask : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Users").child(currentUser?.uid.toString())

        // intent to get the task data
        val taskIntent = intent.getParcelableExtra<TaskDataClass>("task")
        val edit = intent.getBooleanExtra("edit", false)
        val popupIntent = intent.getParcelableExtra<TaskDataClass>("popup")
        val popup = intent.getBooleanExtra("popup", false)
        if ((edit && taskIntent != null)) {
            binding.addTaskTitle.text = "Edit Task"
            binding.taskTitle.setText(taskIntent.title)
            binding.taskDescription.setText(taskIntent.description)

            val oldTimeStamp = taskIntent.timeStamp
            binding.done.setOnClickListener {
                val title = binding.taskTitle.text.toString()
                val description = binding.taskDescription.text.toString()
                val timeStamp = System.currentTimeMillis()
                val newTask = TaskDataClass(title, description, timeStamp)
                databaseReference.child("Tasks").child(oldTimeStamp.toString()).removeValue()
                databaseReference.child("Tasks").child(timeStamp.toString()).setValue(newTask)
                finish()
            }
        } else if(popup && popupIntent != null) {
            binding.addTaskTitle.text = "Edit Task"
            binding.taskTitle.setText(popupIntent.title)
            binding.taskDescription.setText(popupIntent.description)

            val oldTimeStamp = popupIntent.timeStamp
            binding.done.setOnClickListener {
                val title = binding.taskTitle.text.toString()
                val description = binding.taskDescription.text.toString()
                val timeStamp = System.currentTimeMillis()
                val newTask = TaskDataClass(title, description, timeStamp)
                databaseReference.child("Tasks").child(oldTimeStamp.toString()).removeValue()
                databaseReference.child("Tasks").child(timeStamp.toString()).setValue(newTask)
                finish()
            }
        } else {

            // Set up the save button
            binding.done.setOnClickListener {
                val title = binding.taskTitle.text.toString()
                val description = binding.taskDescription.text.toString()
                val timeStamp = System.currentTimeMillis()
                val task = TaskDataClass(title, description, timeStamp)
                databaseReference.child("Tasks").child(timeStamp.toString()).setValue(task)
                finish()
            }
        }
    }
}