package com.example.wishlistconnect

import android.content.Intent
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class RegActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)

        val userName: EditText = findViewById(R.id.user_name)
        val userEmail: EditText = findViewById(R.id.user_mail)
        val userPass: EditText = findViewById(R.id.user_pass)
        val button: Button = findViewById(R.id.button_reg)
        val linkToAuth: TextView = findViewById(R.id.link_to_auth)

        linkToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val pass = userPass.text.toString().trim()
            val name = userName.text.toString().trim()

            if(email == "" || pass == "" || name == "")
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            else {
                val dbHelper = DatabaseHelper(this)
                val userExists = dbHelper.verifyUserByEmail(email)

                if (userExists) {
                    Toast.makeText(this, "User already registered", Toast.LENGTH_SHORT).show()
                } else {
                    // Generate a unique ID (assuming the ID is auto-incremented in the database)
                    // Alternatively, you can use a UUID or any other unique ID generation method
                    val uniqueId = dbHelper.addUser(User(0, name, email, pass))

                    if (uniqueId != -1L) {
                        Toast.makeText(this, "User registered with ID: $uniqueId", Toast.LENGTH_SHORT).show()

                        userEmail.text.clear()
                        userPass.text.clear()
                        userName.text.clear()

                        val userManager = UserManager(this)

                        // After successful login
                        userManager.loginUser(uniqueId)

                        val intent = Intent(this, MenuActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}