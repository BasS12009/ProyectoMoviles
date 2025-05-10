package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridView
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import mx.edu.itson.potros.wrapsy.Entities.Basket
import mx.edu.itson.potros.wrapsy.data.BasketDAO


class BasketActivity : BaseActivity() {

    private lateinit var btnPayOrder: Button
    private lateinit var basketDao: BasketDAO
    private lateinit var basketListView: GridView
    private lateinit var giftBasketAdapter: GiftBasketAdapter
    private lateinit var userId: String
    private var basket: Basket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BasketActivity", "onCreate() started")
        setContentView(R.layout.activity_basket)

        btnPayOrder = findViewById(R.id.btn_pay_order)
        basketListView = findViewById(R.id.basket_list_view)
        basketDao = BasketDAO()
        userId = getCurrentUserId()

        setupBottomNavigation()
        setupTopBarNavigation()

        Log.d("BasketActivity", "Views initialized")

        btnPayOrder.setOnClickListener {
            val intent = Intent(this@BasketActivity, BasketTotalActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("BasketActivity", "onResume() called - refreshing basket data")
        loadBasketDataFromFirestore()
    }

    private fun loadBasketDataFromFirestore() {
        Log.d("BasketActivity", "loadBasketDataFromFirestore() started for user: $userId")
        lifecycleScope.launch {
            basket = basketDao.getBasketForUser(userId)
            basket?.let {
                Log.d("BasketActivity", "Basket found for user $userId. Gift IDs: ${it.giftIds}")
                giftBasketAdapter = GiftBasketAdapter(
                    this@BasketActivity,
                    it.giftIds.toMutableList(),
                    basketDao,
                    userId
                )
                basketListView.adapter = giftBasketAdapter
                updateTotalPrice(it.giftIds)
            } ?: run {
                Log.d("BasketActivity", "No basket found for user $userId or basket is empty.")
                giftBasketAdapter = GiftBasketAdapter(this@BasketActivity, mutableListOf(), basketDao, userId)
                basketListView.adapter = giftBasketAdapter
                updateTotalPrice(emptyList())
            }
        }
    }

     fun updateTotalPrice(giftIds: List<String>) {
        Log.d("BasketActivity", "updateTotalPrice() called with ${giftIds.size} gift IDs.")
        lifecycleScope.launch {
            var totalPrice = 0.0
            if (giftIds.isNotEmpty()) {
                val gifts = basketDao.getAllGiftsInBasketForUser(userId)
                gifts.forEach { totalPrice += it.price }
            }
            Log.d("BasketActivity", "Total price updated: $totalPrice")
        }
    }

    fun getCurrentUserId(): String {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""
    }
}