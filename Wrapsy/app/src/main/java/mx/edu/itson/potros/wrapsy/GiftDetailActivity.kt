package mx.edu.itson.potros.wrapsy

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

        giftsDAO = GiftsDAO()
        setupUI()
        initializeViews()
        setupRecyclerView()
        loadGiftDetails()
    }

    private fun initializeViews() {
        giftImageView = findViewById(R.id.iv_gift_image_cell)
        giftNameTextView = findViewById(R.id.tv_gift_name_cell)
        giftDescTextView = findViewById(R.id.tv_gift_desc_cell)
        giftPriceTextView = findViewById(R.id.tv_gift_price_cell)
        giftRatingTextView = findViewById(R.id.tv_gift_rating_cell)
        addToBasketButton = findViewById(R.id.btn_add_to_basket)
        saveItemLink = findViewById(R.id.tv_filters)
        commentsRecycler = findViewById(R.id.comments_recycler)

        addToBasketButton.setOnClickListener {
            addToBasket(currentGift)
            Toast.makeText(this, "Added to basket!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, BasketActivity::class.java)
            startActivity(intent)
        }

        saveItemLink.setOnClickListener {
            Toast.makeText(this, "Item saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addToBasket(gift: Gift) {
        val basketListJson = sharedPreferences.getString("basket_items", null)
        val basketList = if (basketListJson.isNullOrEmpty()) {
            mutableListOf<Gift>()
        } else {
            val type = object : TypeToken<MutableList<Gift>>() {}.type
            gson.fromJson(basketListJson, type)
        }

        basketList.add(gift)

        val updatedBasketListJson = gson.toJson(basketList)
        sharedPreferences.edit().putString("basket_items", updatedBasketListJson).apply()
    }

    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter(emptyList())
        commentsRecycler.apply {
            layoutManager = GridLayoutManager(this@GiftDetailActivity, 2)
            adapter = commentAdapter
        }
    }

    private fun loadGiftDetails() {
        giftId = intent.getStringExtra("GIFT_ID") ?: ""
        if (giftId.isEmpty()) {
            Toast.makeText(this, "Error: No gift specified", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        giftsDAO.getGift(giftId) { gift ->
            if (gift != null) {
                currentGift = gift
                updateUI(gift)
            } else {
                Toast.makeText(this, "Error: Gift not found", Toast.LENGTH_SHORT).show()
                finish()
            }
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
            }
            .addOnFailureListener { exception ->
                Log.e("GiftDetailActivity", "Error loading comments", exception)
            }
    }

    private fun updateUI(gift: Gift) {
        giftNameTextView.text = gift.name
        giftDescTextView.text = gift.description
        giftPriceTextView.text = "$${gift.price}"
        giftRatingTextView.text = gift.rating.toString()

        if (gift.imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(gift.imageUrl)
                .placeholder(R.drawable.velvet_lipstick)
                .error(R.drawable.velvet_lipstick)
                .into(giftImageView)
        }
    }

    // Mantén solo estas funciones de navegación
    private fun setupUI() {
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
        setSelectedItem(R.id.nav_stores)
    }
}