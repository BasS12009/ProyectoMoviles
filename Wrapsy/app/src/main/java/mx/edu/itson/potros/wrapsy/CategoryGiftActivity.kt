package mx.edu.itson.potros.wrapsy


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.GridView
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
    private lateinit var searchBar: EditText
    private var originalGiftList: MutableList<Gift> = mutableListOf()
    private lateinit var adapter: CategoryGiftAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_gift)

        setupBottomNavigation()


        gridView = findViewById(R.id.gifts_category)
        emptyView = findViewById(R.id.empty_view)
        searchBar = findViewById(R.id.search_bar)
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

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterGifts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun filterGifts(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalGiftList
        } else {
            originalGiftList.filter { gift ->

                gift.name.contains(query, ignoreCase = true)
            }
        }
        adapter.clear()
        adapter.addAll(filteredList)
        adapter.notifyDataSetChanged()

        if (filteredList.isEmpty()) {
            emptyView.text = "No se encontraron productos con ese nombre"
            emptyView.visibility = View.VISIBLE
            gridView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            gridView.visibility = View.VISIBLE
        }
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
        emptyView.text = "Cargando..."
        emptyView.visibility = View.VISIBLE

        giftsDAO.getGiftsByCategory(category) { gifts ->
            if (gifts.isNotEmpty()) {
                emptyView.visibility = View.GONE
                originalGiftList.addAll(gifts)
                adapter = CategoryGiftAdapter(this, originalGiftList.toMutableList())
                gridView.adapter = adapter

                gridView.setOnItemClickListener { _, _, position, _ ->
                    val gift = adapter.getItem(position) as Gift
                    navigateToGiftDetail(gift)
                }

                searchBar.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        filterGifts(s.toString())
                    }

                    override fun afterTextChanged(s: Editable?) {
                    }
                })

            } else {
                emptyView.text = "No se encontraron productos en esta categoría"
                emptyView.visibility = View.VISIBLE
                gridView.visibility = View.GONE
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
}