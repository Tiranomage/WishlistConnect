package com.example.wishlistconnect

import android.content.Intent
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
                val db = DBHelper(this, null)
                val isMailed = db.getMail(email)

                if(isMailed) {
                    Toast.makeText(this, "Данный пользователь зарегестрирован", Toast.LENGTH_LONG).show()
                } else {
                    val user = User(email, pass, name)
                    db.addUser(user)
                    Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_LONG).show()

                    userEmail.text.clear()
                    userPass.text.clear()
                    userName.text.clear()

                    /*val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)*/
                }
            }
        }
    }
}