package mx.edu.itson.potros.wrapsy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mx.edu.itson.potros.wrapsy.Adapters.CommentAdapter
import mx.edu.itson.potros.wrapsy.DAOs.GiftsDAO
import mx.edu.itson.potros.wrapsy.Entities.Comment
import mx.edu.itson.potros.wrapsy.Entities.Gift
import java.util.Date

class GiftDetailActivity : BaseActivity() {

    private lateinit var giftsDAO: GiftsDAO
    private var giftId: String = ""
    private lateinit var currentGift: Gift
    private lateinit var commentsRecycler: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    // UI Elements
    private lateinit var giftImageView: ImageView
    private lateinit var giftNameTextView: TextView
    private lateinit var giftDescTextView: TextView
    private lateinit var giftPriceTextView: TextView
    private lateinit var giftRatingTextView: TextView
    private lateinit var addToBasketButton: Button
    private lateinit var saveItemLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_details)

        // Initialize shared preferences early
        sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        giftsDAO = GiftsDAO()
        setupUI()
        initializeViews()
        setupRecyclerView()

        // Add debug log
        Log.d("GiftDetailActivity", "Activity created, loading gift details...")

        loadGiftDetails()
    }

    private fun initializeViews() {
        try {
            giftImageView = findViewById(R.id.iv_gift_image_cell)
            giftNameTextView = findViewById(R.id.tv_gift_name_cell)
            giftDescTextView = findViewById(R.id.tv_gift_desc_cell)
            giftPriceTextView = findViewById(R.id.tv_gift_price_cell)
            giftRatingTextView = findViewById(R.id.tv_gift_rating_cell)
            addToBasketButton = findViewById(R.id.btn_add_to_basket)
            saveItemLink = findViewById(R.id.tv_filters)
            commentsRecycler = findViewById(R.id.comments_recycler)

            addToBasketButton.setOnClickListener {
                try {
                    addToBasket(currentGift)
                    Toast.makeText(this, "Added to basket!", Toast.LENGTH_SHORT).show()
                    // Don't immediately navigate to the basket - let the user see the confirmation
                    // Only navigate if explicitly requested
                } catch (e: Exception) {
                    Log.e("GiftDetailActivity", "Error adding to basket", e)
                    Toast.makeText(this, "Error adding to basket: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            saveItemLink.setOnClickListener {
                Toast.makeText(this, "Item saved!", Toast.LENGTH_SHORT).show()
            }

            Log.d("GiftDetailActivity", "Views initialized successfully")
        } catch (e: Exception) {
            Log.e("GiftDetailActivity", "Error initializing views", e)
        }
    }

    private fun addToBasket(gift: Gift) {
        try {
            // No need to check if initialized since we initialize in onCreate
            val basketListJson = sharedPreferences.getString("basket_items", null)
            val basketList = if (basketListJson.isNullOrEmpty()) {
                Log.d("GiftDetailActivity", "Creating new basket list")
                mutableListOf<Gift>()
            } else {
                Log.d("GiftDetailActivity", "Loading existing basket list: $basketListJson")
                val type = object : TypeToken<MutableList<Gift>>() {}.type
                gson.fromJson(basketListJson, type)
            }

            basketList.add(gift)
            Log.d("GiftDetailActivity", "Added ${gift.name} to basket, total items: ${basketList.size}")

            val updatedBasketListJson = gson.toJson(basketList)
            sharedPreferences.edit().putString("basket_items", updatedBasketListJson).apply()
            Log.d("GiftDetailActivity", "Saved updated basket to preferences")
        } catch (e: Exception) {
            Log.e("GiftDetailActivity", "Exception in addToBasket", e)
            throw e // Rethrow to handle in the caller
        }
    }

    private fun setupRecyclerView() {
        try {
            commentAdapter = CommentAdapter(emptyList())
            commentsRecycler.apply {
                layoutManager = GridLayoutManager(this@GiftDetailActivity, 2)
                adapter = commentAdapter
            }
            Log.d("GiftDetailActivity", "RecyclerView setup complete")
        } catch (e: Exception) {
            Log.e("GiftDetailActivity", "Error setting up RecyclerView", e)
        }
    }

    private fun loadGiftDetails() {
        try {
            // Log all intent extras for debugging
            intent.extras?.keySet()?.forEach { key ->
                Log.d("GiftDetailActivity", "Intent extra: $key = ${intent.extras?.get(key)}")
            }

            giftId = intent.getStringExtra("GIFT_ID") ?: ""
            Log.d("GiftDetailActivity", "Retrieved gift ID: $giftId")

            if (giftId.isEmpty()) {
                Log.e("GiftDetailActivity", "Error: No gift ID found in intent extras")
                Toast.makeText(this, "Error: No gift specified", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            giftsDAO.getGift(giftId) { gift ->
                if (gift != null) {
                    Log.d("GiftDetailActivity", "Successfully loaded gift: ${gift.name}")
                    currentGift = gift
                    updateUI(gift)
                    loadComments(gift)
                } else {
                    Log.e("GiftDetailActivity", "Error: Gift not found with ID: $giftId")
                    Toast.makeText(this, "Error: Gift not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } catch (e: Exception) {
            Log.e("GiftDetailActivity", "Exception in loadGiftDetails", e)
            Toast.makeText(this, "Error loading gift details: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    private fun loadComments(gift: Gift) {
        FirebaseFirestore.getInstance().collection("Comments")
            .whereEqualTo("giftId", gift.id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val comments = querySnapshot.documents.mapNotNull { doc ->
                    Comment(
                        id = doc.id,
                        giftId = doc.getString("giftId") ?: "",
                        userName = doc.getString("userName") ?: "Anónimo",
                        text = doc.getString("text") ?: "",
                        rating = doc.getLong("rating")?.toInt() ?: 0,
                        timestamp = doc.getDate("timestamp") ?: Date()
                    )
                }
                commentAdapter.updateComments(comments)
                Log.d("GiftDetailActivity", "Loaded ${comments.size} comments")
            }
            .addOnFailureListener { exception ->
                Log.e("GiftDetailActivity", "Error loading comments", exception)
            }
    }

    private fun updateUI(gift: Gift) {
        try {
            giftNameTextView.text = gift.name
            giftDescTextView.text = gift.description
            giftPriceTextView.text = "${gift.price}"
            giftRatingTextView.text = gift.rating.toString()

            // Handle the image resource ID instead of URL
            if (gift.imageResourceId != 0) {
                try {
                    // Try to load the resource directly first
                    giftImageView.setImageResource(gift.imageResourceId)
                    Log.d("GiftDetailActivity", "Loading image from resource ID: ${gift.imageResourceId}")
                } catch (e: Exception) {
                    // If that fails, try using Glide
                    Log.e("GiftDetailActivity", "Error loading image resource directly, trying with Glide", e)
                    Glide.with(this)
                        .load(gift.imageResourceId)
                        .placeholder(R.drawable.ic_launcher_foreground)  // Use any available drawable
                        .error(R.drawable.ic_launcher_foreground)        // Use any available drawable
                        .into(giftImageView)
                }
            } else {
                // Set a default image if no resource ID is provided
                giftImageView.setImageResource(R.drawable.ic_launcher_foreground)
                Log.d("GiftDetailActivity", "No image resource ID provided, using default")
            }

            Log.d("GiftDetailActivity", "UI updated with gift details")
        } catch (e: Exception) {
            Log.e("GiftDetailActivity", "Error updating UI", e)
            // Try to continue without crashing the app
            Toast.makeText(this, "Error displaying some content: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI() {
        try {
            findViewById<ImageView>(R.id.btn_basket).setOnClickListener {
                startActivity(Intent(this, BasketActivity::class.java))
            }
            findViewById<ImageView>(R.id.btn_notificaciones).setOnClickListener {
                startActivity(Intent(this, NotificationsActivity::class.java))
            }
            findViewById<ImageView>(R.id.more_options).setOnClickListener {
                startActivity(Intent(this, MoreOptions::class.java))
            }
            setupBottomNavigation()
            setSelectedItem(R.id.nav_stores)  // Use the correct menu item ID

            Log.d("GiftDetailActivity", "UI setup complete")
        } catch (e: Exception) {
            Log.e("GiftDetailActivity", "Error in setupUI", e)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("GiftDetailActivity", "Activity resumed")
    }

    override fun onPause() {
        super.onPause()
        Log.d("GiftDetailActivity", "Activity paused")
    }
}