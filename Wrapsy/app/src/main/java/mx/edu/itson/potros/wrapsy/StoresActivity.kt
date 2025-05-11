package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.itson.potros.wrapsy.Adapters.GiftAdapter
import mx.edu.itson.potros.wrapsy.DAOs.GiftsDAO
import mx.edu.itson.potros.wrapsy.Entities.Gift

class StoresActivity : BaseActivity() {

    private lateinit var giftsDAO: GiftsDAO
    private lateinit var categoriesMap: MutableMap<String, List<Gift>>
    private val allCategories = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stores)



        giftsDAO = GiftsDAO()
        categoriesMap = mutableMapOf()

        setupUI()
        loadAllCategories()
    }

    private fun setupUI() {

        // Set up category navigation buttons with IMPROVED category names
        findViewById<TextView>(R.id.btn_details).setOnClickListener {
            Log.d("Navigation", "Navigating to details category")
            navigateToCategory("details")
        }

        findViewById<TextView>(R.id.btn_balloons).setOnClickListener {
            Log.d("Navigation", "Navigating to balloons category")
            navigateToCategory("balloons")
        }

        findViewById<TextView>(R.id.btn_plush_toys).setOnClickListener {
            Log.d("Navigation", "Navigating to plush_toys category")
            navigateToCategory("plush_toys")
        }

        findViewById<TextView>(R.id.btn_mugs).setOnClickListener {
            Log.d("Navigation", "Navigating to mugs category")
            navigateToCategory("mugs")
        }

        setupBottomNavigation()
        setupTopBarNavigation()

        setSelectedItem(R.id.nav_stores)
    }

    private fun navigateToCategory(categoryName: String) {
        // Added debug logging
        Log.d("StoresActivity", "Navigating to category: $categoryName")

        val intent = Intent(this, CategoryGiftActivity::class.java).apply {
            putExtra("CATEGORY", categoryName)
        }

        // Force the activity to start with a new task and clear previous instances
        // to avoid any potential stack issues
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        startActivity(intent)

        }

    private fun loadAllCategories() {
        // Get all gifts to extract all available categories
        FirebaseFirestore.getInstance().collection("Gifts")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Extract all unique categories
                for (document in querySnapshot.documents) {
                    val gift = Gift.fromDocumentSnapshot(document)
                    allCategories.addAll(gift.categories)
                }

                // Print available categories for debugging
                Log.d("StoresActivity", "Available categories: $allCategories")

                // Once we have all categories, load gifts for each one
                loadCategorizedGifts()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error loading categories", exception)
                Toast.makeText(this, "Error loading gifts", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadCategorizedGifts() {
        // Load gifts for each of our predefined categories
        loadCategoryGifts("details", findViewById(R.id.gv_productos_details))
        loadCategoryGifts("balloons", findViewById(R.id.gv_productos_balloons))
        loadCategoryGifts("plush_toys", findViewById(R.id.gv_productos_plush_toys))
        loadCategoryGifts("mugs", findViewById(R.id.gv_productos_mugs))
    }

    private fun loadCategoryGifts(category: String, gridView: GridView) {
        giftsDAO.getGiftsByCategory(category) { gifts ->
            if (gifts.isNotEmpty()) {
                categoriesMap[category] = gifts

                val adapter = GiftAdapter(
                    this,
                    gifts.take(3).toMutableList()
                )
                gridView.adapter = adapter

                gridView.setOnItemClickListener { _, _, position, _ ->
                    val gift = adapter.getItem(position) as Gift
                    navigateToGiftDetail(gift)
                }
            } else {
                gridView.visibility = View.GONE

                // Find and hide the corresponding header button
                when (category) {
                    "details" -> findViewById<TextView>(R.id.btn_details).visibility = View.GONE
                    "balloons" -> findViewById<TextView>(R.id.btn_balloons).visibility = View.GONE
                    "plush_toys" -> findViewById<TextView>(R.id.btn_plush_toys).visibility = View.GONE
                    "mugs" -> findViewById<TextView>(R.id.btn_mugs).visibility = View.GONE
                }
            }
        }
    }

    private fun navigateToGiftDetail(gift: Gift) {
        try {
            Log.d("CategoryGiftActivity", "Attempting to navigate to gift detail: ${gift.id}")
            val intent = Intent(this, GiftDetailActivity::class.java).apply {
                putExtra("GIFT_ID", gift.id)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            Log.d("CategoryGiftActivity", "Intent created with GIFT_ID: ${gift.id}")
            startActivity(intent)
            Log.d("CategoryGiftActivity", "startActivity called successfully")
        } catch (e: Exception) {
            Log.e("CategoryGiftActivity", "Error navigating to gift detail", e)
            Toast.makeText(this, "Error opening gift details: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("StoresActivity", "onResume called")
    }
}