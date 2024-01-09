package com.example.wishlistconnect

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "wishlist.db"
        private const val DATABASE_VERSION = 1

        // Table names
        const val TABLE_USERS = "Users"
        const val TABLE_WISHLISTS = "Wishlists"
        const val TABLE_GIFTS = "Gifts"

        // Common columns
        const val COLUMN_ID = "id"

        // Users table columns
        const val COLUMN_USER_NAME = "name"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_PASSWORD = "password"

        // Wishlists table columns
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_GIFT_ID = "gift_id"

        // Gifts table columns
        const val COLUMN_GIFT_NAME = "gift_name"
        const val COLUMN_GIFT_DESCRIPTION = "description"
        const val COLUMN_GIFT_PRICE = "price"

        // Editable columns for Users
        val EDITABLE_USER_COLUMNS = arrayOf(COLUMN_USER_NAME, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD)

        // Editable columns for Gifts
        val EDITABLE_GIFT_COLUMNS = arrayOf(COLUMN_GIFT_NAME, COLUMN_GIFT_DESCRIPTION, COLUMN_GIFT_PRICE)
    }

    // Create tables
    private val CREATE_TABLE_USERS = """
        CREATE TABLE $TABLE_USERS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_USER_NAME TEXT NOT NULL,
            $COLUMN_USER_EMAIL TEXT UNIQUE NOT NULL,
            $COLUMN_USER_PASSWORD TEXT NOT NULL
        );
    """.trimIndent()

    private val CREATE_TABLE_GIFTS = """
        CREATE TABLE $TABLE_GIFTS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_GIFT_NAME TEXT NOT NULL,
            $COLUMN_GIFT_DESCRIPTION TEXT,
            $COLUMN_GIFT_PRICE REAL
        );
    """.trimIndent()

    private val CREATE_TABLE_WISHLISTS = """
        CREATE TABLE $TABLE_WISHLISTS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_USER_ID INTEGER,
            $COLUMN_GIFT_ID INTEGER,
            FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID),
            FOREIGN KEY($COLUMN_GIFT_ID) REFERENCES $TABLE_GIFTS($COLUMN_ID)
        );
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_USERS)
        db.execSQL(CREATE_TABLE_GIFTS)
        db.execSQL(CREATE_TABLE_WISHLISTS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WISHLISTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GIFTS")

        // Create tables again
        onCreate(db)
    }

    // Function to add a new user
    fun addUser(user: User): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_NAME, user.name)
            put(COLUMN_USER_EMAIL, user.email)
            put(COLUMN_USER_PASSWORD, user.password)
        }

        val userId = db.insert(TABLE_USERS, null, values)
        db.close()
        return userId
    }

    // Function to verify user by email
    fun verifyUserByEmail(email: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        val userExists = cursor.count > 0
        cursor.close()
        db.close()
        return userExists
    }

    // Function to update user information
    fun updateUser(userId: Long, values: ContentValues): Boolean {
        val db = writableDatabase
        val affectedRows = db.update(TABLE_USERS, values, "$COLUMN_ID=?", arrayOf(userId.toString()))
        db.close()
        return affectedRows > 0
    }

    fun addGift(gift: Gift): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_GIFT_NAME, gift.name)
            put(COLUMN_GIFT_DESCRIPTION, gift.description)
            put(COLUMN_GIFT_PRICE, gift.price)
        }
        val id = db.insert(TABLE_GIFTS, null, values)
        db.close()
        return id
    }

    fun getGifts(): List<Gift> {
        val db = readableDatabase
        val cursor = db.query(TABLE_GIFTS, null, null, null, null, null, null)

        val gifts = mutableListOf<Gift>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_GIFT_NAME))
                val description = cursor.getString(cursor.getColumnIndex(COLUMN_GIFT_DESCRIPTION))
                val price = cursor.getDouble(cursor.getColumnIndex(COLUMN_GIFT_PRICE))

                gifts.add(Gift(id, name, description, price))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return gifts
    }

    // Function to update gift information
    fun updateGift(giftId: Long, values: ContentValues): Boolean {
        val db = writableDatabase
        val affectedRows = db.update(TABLE_GIFTS, values, "$COLUMN_ID=?", arrayOf(giftId.toString()))
        db.close()
        return affectedRows > 0
    }

    // Function to delete a gift from the wishlist
    fun deleteGiftFromWishlist(userId: Long, giftId: Long): Boolean {
        val db = writableDatabase
        val affectedRows = db.delete(TABLE_WISHLISTS, "$COLUMN_USER_ID=? AND $COLUMN_GIFT_ID=?", arrayOf(userId.toString(), giftId.toString()))
        db.close()
        return affectedRows > 0
    }

    // Function to retrieve all gifts in a user's wishlist
    fun getWishlistForUser(userId: Long): List<Gift> {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_WISHLISTS " +
                "INNER JOIN $TABLE_GIFTS ON $TABLE_WISHLISTS.$COLUMN_GIFT_ID = $TABLE_GIFTS.$COLUMN_ID " +
                "WHERE $TABLE_WISHLISTS.$COLUMN_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        val wishlist = mutableListOf<Gift>()

        if (cursor.moveToFirst()) {
            do {
                val gift = Gift(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_GIFT_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_GIFT_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_GIFT_DESCRIPTION)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_GIFT_PRICE))
                )
                wishlist.add(gift)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return wishlist
    }

    // Function to retrieve a user by email and password (for authentication)
    fun getUserByEmailAndPassword(email: String, password: String): User? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))

        val user: User? = if (cursor.moveToFirst()) {
            User(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)),
                cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD))
            )
        } else {
            null
        }

        cursor.close()
        db.close()
        return user
    }
}
