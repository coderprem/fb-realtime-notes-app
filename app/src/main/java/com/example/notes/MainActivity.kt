package com.example.notes

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.authentication.SignUpActivity
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
    private var sortByAsc: Boolean = true
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedEdit: SharedPreferences.Editor

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

        // initialize the shared preferences
        sharedPreferences = getSharedPreferences("Notes", MODE_PRIVATE)
        sharedEdit = sharedPreferences.edit()
        sortByAsc = sharedPreferences.getBoolean("sortByAsc", true)

        // empty image visibility
        binding.emptyImage.visibility = if (taskList.isEmpty()) View.VISIBLE else View.GONE

        // Add a child event listener to the database reference
        databaseReference.child("Tasks").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val taskData = snapshot.getValue(TaskDataClass::class.java)
                if (taskData != null) {
                    taskList.add(taskData)
                    if (!sortByAsc) {
                        taskList.sortBy { it.timeStamp }
                        taskList.reverse()
                    }
                    taskAdapter.notifyDataSetChanged()
                    binding.emptyImage.visibility = if (taskList.isEmpty()) View.VISIBLE else View.GONE
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
                    binding.emptyImage.visibility = if (taskList.isEmpty()) View.VISIBLE else View.GONE
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

        // scroll listener for the recycler view
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    binding.addTask.shrink()
                } else {
                    binding.addTask.extend()
                }
            }
        })

        // Set up the more options button
        binding.moreOptions.setOnClickListener {
            // Show a popup menu, with options to sort in asc and desc, sign out, and delete all tasks
            moreOptionPopupMenu()
        }
    }

    private fun moreOptionPopupMenu() {
        val popupMenu = PopupMenu(this, binding.moreOptions)
        popupMenu.menu.add("Sort by ${toggleAscText()}")
        popupMenu.menu.add("Sign Out")
        popupMenu.menu.add("Delete All")
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                "Sort by ${toggleAscText()}" -> {
                    // Sort the tasks
                    sortByAsc = !sortByAsc
                    sharedEdit.putBoolean("sortByAsc", sortByAsc)
                    sharedEdit.apply()
                    taskList.sortBy { it.timeStamp }
                    if (!sortByAsc) {
                        taskList.reverse()
                    }
                    taskAdapter.notifyDataSetChanged()
                }
                "Sign Out" -> {
                    // Sign out the user
                    firebaseAuth.signOut()
                    val intent = Intent(this, SignUpActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "Delete All" -> {
                    // Delete all tasks
                    databaseReference.child("Tasks").removeValue()
                }
            }
            true
        }
        popupMenu.show()
    }
    private fun toggleAscText() :String {
        return if (sortByAsc) {
            "Desc"
        } else {
            "Asc"
        }
    }
}