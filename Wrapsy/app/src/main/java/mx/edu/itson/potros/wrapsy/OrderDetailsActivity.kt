package mx.edu.itson.potros.wrapsy

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.Entities.Purchase

class OrderDetailsActivity : BaseActivity() {

    private lateinit var llGifts: LinearLayout
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        setupBottomNavigation()
        setupTopBarNavigation()
        setSelectedItem(R.id.nav_orders)

        llGifts = findViewById(R.id.ll_gifts)
        val purchaseId = intent.getStringExtra("PURCHASE_ID") ?: run {
            showErrorMessage("Invalid order", "#FF0000")
            finish()
            return
        }

        loadPurchaseDetails(purchaseId)
    }

    private fun loadPurchaseDetails(purchaseId: String) {
        db.collection("Purchase").document(purchaseId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val purchase = document.toObject(Purchase::class.java)?.apply {
                        id = document.id
                    }
                    purchase?.let {
                        updateUI(it)
                        loadGifts(it.giftIds)
                    } ?: showErrorMessage("Order data corrupted", "#FF0000")
                } else {
                    showErrorMessage("Order not found", "#FF0000")
                }
            }
            .addOnFailureListener {
                showErrorMessage("Connection error: ${it.message}", "#FF0000")
            }
    }

    private fun updateUI(purchase: Purchase) {
        // Actualizar detalles principales
        findViewById<TextView>(R.id.tv_status_detail).text = "Status: ${purchase.status}"
        findViewById<TextView>(R.id.tv_order_number).text = "Order #${purchase.orderNumber}"
        findViewById<TextView>(R.id.tv_quantity).text = "Items: ${purchase.quantity}"
        findViewById<TextView>(R.id.tv_order_date).text = "Order Date: ${purchase.orderDate}"
        findViewById<TextView>(R.id.tv_estimated_delivery).text = "Delivery: ${purchase.estimatedDelivery}"
        findViewById<TextView>(R.id.tv_total_cost).text = "Total: $${"%.2f".format(purchase.totalCost)}"

        // Color del estado
        val statusColor = when(purchase.status) {
            "Shipped" -> "#4CAF50"  // Verde
            "Delivered" -> "#9370DB" // Morado
            else -> "#9E9E9E"         // Gris
        }
        findViewById<TextView>(R.id.tv_status_detail).setTextColor(Color.parseColor(statusColor))
    }

    private fun loadGifts(giftIds: List<String>) {
        llGifts.removeAllViews()

        if (giftIds.isEmpty()) {
            showEmptyMessage()
            return
        }

        db.collection("Gift")
            .whereIn("__name__", giftIds)
            .get()
            .addOnSuccessListener { result ->
                val gifts = result.documents.mapNotNull {
                    it.toObject(Gift::class.java)?.apply {
                        id = it.id
                        // Convertir Long de Firestore a Int
                        imageResourceId = (it.getLong("imageResourceId")?.toInt()?: 0)
                    }
                }

                if (gifts.isNotEmpty()) {
                    renderGifts(gifts)
                } else {
                    showEmptyMessage()
                }
            }
            .addOnFailureListener {
                showErrorMessage("Failed to load gifts", "#FF0000")
            }
    }

    private fun renderGifts(gifts: List<Gift>) {
        val inflater = LayoutInflater.from(this)

        gifts.forEach { gift ->
            val giftView = inflater.inflate(R.layout.item_purchased_gift, llGifts, false)

            // Configurar vistas
            giftView.findViewById<TextView>(R.id.tv_gift_name).text = gift.name
            giftView.findViewById<TextView>(R.id.tv_price).text = "$${"%.2f".format(gift.price)}"

            // Cargar imagen desde recursos locales
            Glide.with(this)
                .load(gift.imageResourceId)  // ID de recurso drawable
                .placeholder(R.drawable.heisenberg_plush)
                .into(giftView.findViewById(R.id.iv_purchase))

            llGifts.addView(giftView)
        }
    }

    private fun showEmptyMessage() {
        val textView = TextView(this).apply {
            text = "No gifts in this order"
            textSize = 16f
            setTextColor(Color.parseColor("#616161")) // Gris oscuro
            setPadding(0, dpToPx(32), 0, 0)
        }
        llGifts.addView(textView)
    }

    private fun showErrorMessage(message: String, colorHex: String) {
        val textView = TextView(this).apply {
            text = message
            textSize = 16f
            setTextColor(Color.parseColor(colorHex))
            setPadding(0, dpToPx(32), 0, 0)
        }
        llGifts.addView(textView)
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
}