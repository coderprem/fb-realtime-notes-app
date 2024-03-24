package com.example.notes.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notes.MainActivity
import com.example.notes.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // check if user is already signed in
        if (firebaseAuth.currentUser != null) {
            // navigate to notes activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // navigate to login activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}