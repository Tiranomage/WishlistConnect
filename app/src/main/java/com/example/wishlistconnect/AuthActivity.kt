package com.example.wishlistconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val userEmail: EditText = findViewById(R.id.user_mail_auth)
        val userPass: EditText = findViewById(R.id.user_pass_auth)
        val button: Button = findViewById(R.id.button_auth)
        val linkToReg: TextView = findViewById(R.id.link_to_reg)

        linkToReg.setOnClickListener {
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val pass = userPass.text.toString().trim()

            if(email == "" || pass == "")
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            else {

                val db = DatabaseHelper(this)
                val isAuth = db.getUserByEmailAndPassword(email, pass)
                if(isAuth != null){
                    Toast.makeText(this, "Авторизация успешна", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)

                    userEmail.text.clear()
                    userPass.text.clear()
                } else
                    Toast.makeText(this, "Логин и/или пароль не верны", Toast.LENGTH_LONG).show()
            }
        }
    }
}