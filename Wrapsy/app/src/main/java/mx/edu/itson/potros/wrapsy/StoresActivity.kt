package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        // Setup navigation buttons
        val btnBasket: ImageView = findViewById(R.id.basketIcon)
        val btnNotification: ImageView = findViewById(R.id.btn_notificaciones)
        val btnMoreOption: ImageView = findViewById(R.id.options)

        btnBasket.setOnClickListener {
            val intent = Intent(this, BasketActivity::class.java)
            startActivity(intent)
        }

        btnNotification.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        btnMoreOption.setOnClickListener {
            val intent = Intent(this, MoreOptions::class.java)
            startActivity(intent)
        }

//        // Setup category headers click listeners
//        findViewById<Button>(R.id.linear_layout_categorias).setOnClickListener {
//            val intent = Intent(this, CategoryActivity::class.java).apply {
//                putExtra("CATEGORY", "details")
//            }
//            startActivity(intent)
//        }
//
//        findViewById<Button>(R.id.btn_balloons).setOnClickListener {
//            val intent = Intent(this, CategoryActivity::class.java).apply {
//                putExtra("CATEGORY", "balloons")
//            }
//            startActivity(intent)
//        }
//
//        findViewById<Button>(R.id.btn_plush_toys).setOnClickListener {
//            val intent = Intent(this, CategoryActivity::class.java).apply {
//                putExtra("CATEGORY", "plush toys")
//            }
//            startActivity(intent)
//        }
//
//        findViewById<Button>(R.id.btn_mugs).setOnClickListener {
//            val intent = Intent(this, CategoryActivity::class.java).apply {
//                putExtra("CATEGORY", "mugs")
//            }
//            startActivity(intent)
//        }

        setupBottomNavigation()
        setSelectedItem(R.id.nav_stores)
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
                // Store the gifts in our map
                categoriesMap[category] = gifts

                // Display up to 3 items in the GridView
                val adapter = GiftAdapter(
                    this,
                    gifts.take(3).toMutableList()
                )
                gridView.adapter = adapter

                // Set click listener for each item
                gridView.setOnItemClickListener { _, _, position, _ ->
                    val gift = adapter.getItem(position) as Gift
//                    navigateToGiftDetail(gift)
                }
            } else {
                // Hide this category's GridView if no gifts
                gridView.visibility = View.GONE

                // Find and hide the corresponding header button
                when (category) {
                    "details" -> findViewById<Button>(R.id.linear_layout_categorias).visibility = View.GONE
                    "balloons" -> findViewById<Button>(R.id.btn_balloons).visibility = View.GONE
                    "plush toys" -> findViewById<Button>(R.id.btn_plush_toys).visibility = View.GONE
                    "mugs" -> findViewById<Button>(R.id.btn_mugs).visibility = View.GONE
                }
            }
        }
    }

//    private fun navigateToGiftDetail(gift: Gift) {
//        val intent = Intent(this, GiftDetailActivity::class.java).apply {
//            putExtra("GIFT_ID", gift.id)
//        }
//        startActivity(intent)
//    }
}
