package com.example.wishlistconnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast

class MenuActivity : AppCompatActivity() {

    private lateinit var giftListView: ListView
    private lateinit var btnAddGift: Button
    private lateinit var gifts: List<Gift> // Assuming you have a list of Gift objects
    private lateinit var giftAdapter: GiftAdapter // Assuming you have a custom adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Initialize UI components
        giftListView = findViewById(R.id.giftListView)
        btnAddGift = findViewById(R.id.btnAddGift)

        // Populate the gift list (you should replace this with your actual data retrieval logic)
        val userManager = UserManager(this)
        gifts = DatabaseHelper(this).getWishlistForUser(userManager.getCurrentUserId())

        // Initialize the custom adapter
        giftAdapter = GiftAdapter(this, gifts)

        // Set the adapter to the ListView
        giftListView.adapter = giftAdapter

        // Set click listener for list items
        giftListView.setOnItemClickListener { _, _, position, _ ->
            val selectedGift = gifts[position]
            // Open a detailed page for the selected gift (you should implement this)
            openDetailedPage(selectedGift)
        }

        // Set long click listener for list items (to handle deletion)
        giftListView.setOnItemLongClickListener { _, _, position, _ ->
            val selectedGift = gifts[position]
            val selectedUser = userManager.getCurrentUserId()
            // Show a dialog or perform any action for deletion (you should implement this)
            showDeleteDialog(selectedUser, selectedGift, userManager)
            true
        }

        // Set click listener for "Add New Gift" button
        btnAddGift.setOnClickListener {
            // Open a page for adding a new gift (you should implement this)
            openAddGiftPage()
        }
    }

    // You should replace this with your actual data retrieval logic
    private fun getGiftListFromDatabase(selectedUser: User): List<Gift> {
        // Example: Fetching gifts from a hypothetical database
        val dbHelper = DatabaseHelper(this)
        return dbHelper.getWishlistForUser(selectedUser.id)
    }

    private fun openDetailedPage(selectedGift: Gift) {
        // You should implement this method to open a detailed page for the selected gift
        // For example, you can pass the selectedGift data to a new activity using Intent
        // and display the detailed information on that activity.
        // Example:
        val intent = Intent(this, DetailedGiftActivity::class.java)
        intent.putExtra("gift", selectedGift)
        startActivity(intent)
    }

    private fun openAddGiftPage() {
        // You should implement this method to open a page for adding a new gift
        // For example, you can open a new activity where the user can fill in the details
        // of the new gift.
        // Example:
        val intent = Intent(this, AddGiftActivity::class.java)
        startActivity(intent)
    }

    private fun showDeleteDialog(selectedUser: Long, selectedGift: Gift, userManager: UserManager) {
        // You should implement this method to show a dialog or confirmation box
        // for deleting the selected gift.
        // Example:
        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete Gift")
            .setMessage("Are you sure you want to delete this gift?")
            .setPositiveButton("Yes") { _, _ ->
                // Delete the selected gift from the database (you should implement this)
                deleteGift(selectedUser, selectedGift, userManager)
            }
            .setNegativeButton("No", null)
            .create()
        dialog.show()
    }

    private fun deleteGift(selectedUser: Long, selectedGift: Gift, userManager: UserManager) {
        // You should implement this method to delete the selected gift from the database
        // Example:
        val dbHelper = DatabaseHelper(this)
        dbHelper.deleteGiftFromWishlist(selectedUser, selectedGift.id)
        // Refresh the list after deletion (you should implement this)
        gifts = DatabaseHelper(this).getWishlistForUser(userManager.getCurrentUserId())
        giftAdapter.updateGifts(gifts)
    }
}