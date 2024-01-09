package com.example.wishlistconnect

import android.content.Context
import android.content.SharedPreferences

class UserManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val USER_ID_KEY = "user_id"

    fun loginUser(userId: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong(USER_ID_KEY, userId)
        editor.apply()
    }

    fun logoutUser() {
        val editor = sharedPreferences.edit()
        editor.remove(USER_ID_KEY)
        editor.apply()
    }

    fun getCurrentUserId(): Long {
        return sharedPreferences.getLong(USER_ID_KEY, -1)
    }

    fun isUserLoggedIn(): Boolean {
        return getCurrentUserId() != -1L
    }
}
