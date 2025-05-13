package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.itson.potros.wrapsy.DAOs.GiftsDAO
import mx.edu.itson.potros.wrapsy.Entities.Gift
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BasketTotalActivity : BaseActivity() {

    private lateinit var llOrderGifts: LinearLayout
    private lateinit var tvTotalPrice: TextView
    private lateinit var tvOrderDate: TextView
    private val db = FirebaseFirestore.getInstance()
    private val giftsDao = GiftsDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket_total)

        setupBottomNavigation()

        llOrderGifts = findViewById(R.id.ll_order_gifts)
        tvTotalPrice = findViewById(R.id.tv_total_price)
        tvOrderDate = findViewById(R.id.tv_order_date)

        val btnConfirm: TextView = findViewById(R.id.btn_confirm)

        // Configurar fecha actual
        setCurrentDate()

        // Obtener canasta del usuario
        getBasketItems()

        btnConfirm.setOnClickListener {
            val intent = Intent(this, BasketCardInformationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setCurrentDate() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        tvOrderDate.text = "Order Date: $currentDate"
    }

    private fun getBasketItems() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            db.collection("Basket")
                .whereEqualTo("userId", user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        showEmptyBasket()
                    } else {
                        val basket = documents.first()
                        val giftIds = basket.get("giftIds") as? List<String> ?: emptyList()
                        loadGifts(giftIds)
                    }
                }
                .addOnFailureListener {
                    showError("Error loading basket")
                }
        } ?: showError("User not logged in")
    }

    private fun loadGifts(giftIds: List<String>) {
        if (giftIds.isEmpty()) {
            showEmptyBasket()
            return
        }

        val gifts = mutableListOf<Gift>()
        var totalPrice = 0.0

        giftIds.forEach { giftId ->
            giftsDao.getGift(giftId) { gift ->
                gift?.let {
                    gifts.add(it)
                    totalPrice += it.price

                    if (gifts.size == giftIds.size) {
                        renderGifts(gifts)
                        tvTotalPrice.text = "Total: $${"%.2f".format(totalPrice)}"
                    }
                } ?: showError("Gift $giftId not found")
            }
        }
    }

    private fun renderGifts(gifts: List<Gift>) {
        llOrderGifts.removeAllViews()
        val inflater = LayoutInflater.from(this)

        gifts.forEach { gift ->
            val giftView = inflater.inflate(R.layout.item_basketpay_gift, llOrderGifts, false)

            // Configurar vistas
            giftView.findViewById<TextView>(R.id.tv_basketpay_gift_name).text = gift.name
            giftView.findViewById<TextView>(R.id.tv_basketpay_gift_desc).text = gift.description
            giftView.findViewById<TextView>(R.id.tv_basketpay_gift_price).text = "$${"%.2f".format(gift.price)}"

            // Cargar imagen desde recursos locales
            giftView.findViewById<ImageView>(R.id.iv_basketpay_gift_image)
                .setImageResource(gift.imageResourceId)

            llOrderGifts.addView(giftView)
        }
    }

    private fun showEmptyBasket() {
        llOrderGifts.isVisible = false
        tvTotalPrice.text = "Total: $0.00"
        findViewById<TextView>(R.id.btn_confirm).isEnabled = false
    }

    private fun showError(message: String) {
        val errorView = TextView(this).apply {
            text = message
            setTextColor(Color.RED)
            textSize = 16f
        }
        llOrderGifts.addView(errorView)
    }
}