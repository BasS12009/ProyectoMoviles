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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import mx.edu.itson.potros.wrapsy.Adapters.CommentAdapter
import mx.edu.itson.potros.wrapsy.DAOs.GiftsDAO
import mx.edu.itson.potros.wrapsy.Entities.Comment
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.data.BasketDAO
import java.util.Date

class GiftDetailActivity : BaseActivity() {

    private lateinit var giftsDAO: GiftsDAO
    private var giftId: String = ""
    private lateinit var currentGift: Gift
    private lateinit var commentsRecycler: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val basketDAO = BasketDAO()
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
        initializeViews()
        setupRecyclerView()

        setupBottomNavigation()
        setupTopBarNavigation()

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
        Log.d("GiftDetailActivity", "addToBasket() called with gift ID: ${gift.id}")
        lifecycleScope.launch {
            try {
                val userId = getCurrentUserId()

                if (userId.isNotEmpty() && gift.id.isNotEmpty()) {
                    val isAdded = basketDAO.addGiftToBasket(userId, gift.id)
                    if (isAdded) {
                        Log.d("GiftDetailActivity", "Gift with ID ${gift.id} added to basket in Firestore for user $userId")
                    } else {
                        Log.e("GiftDetailActivity", "Failed to add gift with ID ${gift.id} to basket in Firestore for user $userId")
                    }
                } else {
                    Log.e("GiftDetailActivity", "User ID or Gift ID not available, cannot add to basket in Firestore (User ID: $userId, Gift ID: ${gift.id})")
                }

            } catch (e: Exception) {
                Log.e("GiftDetailActivity", "Exception in addToBasket (Firestore): ${e.message}", e)
            }
        }
    }


    private fun getCurrentUserId(): String {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""
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

    override fun onResume() {
        super.onResume()
        Log.d("GiftDetailActivity", "Activity resumed")
    }

    override fun onPause() {
        super.onPause()
        Log.d("GiftDetailActivity", "Activity paused")
    }
}