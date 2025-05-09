package mx.edu.itson.potros.wrapsy

import mx.edu.itson.potros.wrapsy.BaseActivity
import mx.edu.itson.potros.wrapsy.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import mx.edu.itson.potros.wrapsy.Adapters.CategoryGiftAdapter
import mx.edu.itson.potros.wrapsy.DAOs.GiftsDAO
import mx.edu.itson.potros.wrapsy.Entities.Gift

class CategoryGiftActivity : BaseActivity() {

    private lateinit var giftsDAO: GiftsDAO
    private lateinit var categoryTitle: TextView
    private lateinit var gridView: GridView
    private lateinit var category: String
    private lateinit var emptyView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_gift)


        gridView = findViewById(R.id.gifts_category)
        emptyView = findViewById(R.id.empty_view)
        giftsDAO = GiftsDAO()

        // Get the category from intent
        category = intent.getStringExtra("CATEGORY") ?: ""

        // Log the received category for debugging
        Log.d("CategoryGiftActivity", "Received category: $category")

        if (category.isEmpty()) {
            Toast.makeText(this, "Error: No se especificó categoría", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configure the grid view
        gridView.apply {
            isNestedScrollingEnabled = true
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        // Set up the category title
        categoryTitle = findViewById(R.id.category_title)
        setCategoryTitle(category)

        // Load gifts for this category
        loadCategoryGifts()
    }

    private fun setCategoryTitle(category: String) {
        // Assign a user-friendly title based on the category ID
        val title = when (category) {
            "details" -> "Details"
            "balloons" -> "Balloons"
            "plush_toys" -> "Plush Toys"
            "mugs" -> "Mugs"
            else -> category.capitalize()
        }

        categoryTitle.text = title
    }



    private fun loadCategoryGifts() {
        // Show a loading message
        emptyView.text = "Cargando..."
        emptyView.visibility = View.VISIBLE

        giftsDAO.getGiftsByCategory(category) { gifts ->
            if (gifts.isNotEmpty()) {
                // Hide the empty view
                emptyView.visibility = View.GONE

                // Set up the adapter with the gifts
                val adapter = CategoryGiftAdapter(this, gifts.toMutableList())
                gridView.adapter = adapter

                // Set up click listener for items
                gridView.setOnItemClickListener { _, _, position, _ ->
                    val gift = adapter.getItem(position) as Gift
                    navigateToGiftDetail(gift)
                }
            } else {
                // Show a message if no gifts were found
                emptyView.text = "No se encontraron productos en esta categoría"
                emptyView.visibility = View.VISIBLE
                gridView.visibility = View.GONE
            }
        }
    }

    private fun navigateToGiftDetail(gift: Gift) {
        Log.d("CategoryGiftActivity", "Navigating to gift detail: ${gift.id}")
        val intent = Intent(this, GiftDetailActivity::class.java).apply {
            putExtra("GIFT_ID", gift.id)
        }
        startActivity(intent)
    }
}