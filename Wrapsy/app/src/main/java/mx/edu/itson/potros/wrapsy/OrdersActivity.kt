package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.itson.potros.wrapsy.Entities.Purchase

class OrdersActivity : BaseActivity() {

    private lateinit var llActiveOrders: LinearLayout
    private lateinit var llPastOrders: LinearLayout
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        setupBottomNavigation()
        setSelectedItem(R.id.nav_orders)

        llActiveOrders = findViewById(R.id.ll_active_orders)
        llPastOrders = findViewById(R.id.ll_past_orders)

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            loadPurchases(user.uid, "Shipped", llActiveOrders)
            loadPurchases(user.uid, "Delivered", llPastOrders)
        }
    }

    private fun loadPurchases(userId: String, status: String, container: LinearLayout) {
        db.collection("Purchase")
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", status)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    showEmptyMessage(container)
                } else {
                    val purchases = documents.toObjects(Purchase::class.java)
                    renderPurchases(purchases, container)
                }
            }
            .addOnFailureListener {
                showErrorMessage(container)
            }
    }

    private fun renderPurchases(purchases: List<Purchase>, container: LinearLayout) {
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)

        purchases.forEach { purchase ->
            val purchaseView = inflater.inflate(R.layout.item_purchase, container, false)

            // Configurar datos básicos
            val tvOrderNumber = purchaseView.findViewById<TextView>(R.id.tv_order)
            val tvDeliveryDate = purchaseView.findViewById<TextView>(R.id.tv_delivery_date)
            val tvStatus = purchaseView.findViewById<TextView>(R.id.tv_status)
            val ivPurchase = purchaseView.findViewById<ImageView>(R.id.iv_purchase)

            tvOrderNumber.text = "Order #${purchase.orderNumber}"
            tvDeliveryDate.text = "Arriving: ${purchase.estimatedDelivery}"
            tvStatus.text = purchase.status

            // Cargar imagen del primer gift
            purchase.giftIds.firstOrNull()?.let { giftId ->
                db.collection("Gift").document(giftId)
                    .get()
                    .addOnSuccessListener { document ->
                        document.getString("imageUrl")?.let { imageUrl ->
                            Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.heisenberg_plush)
                                .into(ivPurchase)
                        }
                    }
            }

            // Click Listener
            purchaseView.setOnClickListener {
                navigateToOrderDetails(purchase.id)
            }

            container.addView(purchaseView)
        }
    }

    private fun navigateToOrderDetails(purchaseId: String?) {
        purchaseId?.let {
            val intent = Intent(this, OrderDetailsActivity::class.java).apply {
                putExtra("PURCHASE_ID", it)
            }
            startActivity(intent)
        }
    }

    private fun showEmptyMessage(container: LinearLayout) {
        container.removeAllViews()
        val textView = TextView(this).apply {
            text = "No orders yet"
            textSize = 16f
            setPadding(32, 32, 32, 32)
        }
        container.addView(textView)
    }

    private fun showErrorMessage(container: LinearLayout) {
        container.removeAllViews()
        val textView = TextView(this).apply {
            text = "Error loading orders"
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, R.color.red))
            setPadding(32, 32, 32, 32)
        }
        container.addView(textView)
    }
}