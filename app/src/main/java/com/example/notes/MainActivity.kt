package com.example.notes

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.task.AddTask
import com.example.notes.task.TaskAdapter
import com.example.notes.task.TaskDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: MutableList<TaskDataClass>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        // Initialize the recycler view
        recyclerView = binding.rvTasks
        taskList = mutableListOf()
        taskAdapter = TaskAdapter(taskList)
        recyclerView.adapter = taskAdapter

        // Get the tasks from the database
//        databaseReference.child("Tasks").get().addOnSuccessListener {
//            for (task in it.children) {
//                val taskData = task.getValue(TaskDataClass::class.java)
//                if (taskData != null) {
//                    taskList.add(taskData)
//                }
//            }
//            taskAdapter.notifyDataSetChanged()
//        }

        databaseReference.child("Tasks").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val taskData = snapshot.getValue(TaskDataClass::class.java)
                if (taskData != null) {
                    taskList.add(taskData)
                    taskAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val taskData = snapshot.getValue(TaskDataClass::class.java)
                if (taskData != null) {
                    val index = taskList.indexOfFirst { it.timeStamp == taskData.timeStamp }
                    if (index != -1) {
                        taskList[index] = taskData
                        taskAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val taskData = snapshot.getValue(TaskDataClass::class.java)
                if (taskData != null) {
                    taskList.remove(taskData)
                    taskAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Not needed
            }

            override fun onCancelled(error: DatabaseError) {
                // Not needed
            }
        })

        // add click listener on the recycler view
        taskAdapter.onItemClickListener = {
            // Open the task details screen
            val intent = Intent(this, AddTask::class.java)
            intent.putExtra("edit", true)
            intent.putExtra("task", it)
            startActivity(intent)
        }

        // Set up the FAB
        binding.addTask.setOnClickListener {
            // Handle FAB click
            val intent = Intent(this, AddTask::class.java)
            startActivity(intent)
        }
    }
}