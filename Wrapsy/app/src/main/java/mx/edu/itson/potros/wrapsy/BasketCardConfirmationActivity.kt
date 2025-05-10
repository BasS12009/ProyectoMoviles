package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import mx.edu.itson.potros.wrapsy.DAOs.GiftsDAO
import mx.edu.itson.potros.wrapsy.DAOs.PurchaseDAO
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.Entities.Purchase
import java.text.SimpleDateFormat
import java.util.*

class BasketCardConfirmationActivity : BaseActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var giftsDao: GiftsDAO
    private lateinit var purchaseDao: PurchaseDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket_card_information)

        setupBottomNavigation()

        db = FirebaseFirestore.getInstance()
        giftsDao = GiftsDAO()
        purchaseDao = PurchaseDAO()

        val btnConfirm = findViewById<TextView>(R.id.tv_confirm_button)
        btnConfirm.setOnClickListener {
            processPayment()
        }
    }

    private fun processPayment() {
        if (validateForm()) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let { user ->
                // Obtener canasta del usuario
                db.collection("basket")
                    .whereEqualTo("user_id", user.uid)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            val basketDoc = documents.first()
                            val giftIds = basketDoc["gift_ids"] as? List<String> ?: emptyList()

                            if (giftIds.isNotEmpty()) {
                                // Obtener detalles de los gifts
                                giftsDao.getGiftsByIds(giftIds) { gifts ->
                                    createPurchase(user.uid, giftIds, gifts, basketDoc)
                                }
                            } else {
                                showError("Basket is empty")
                            }
                        }
                    }
            } ?: showError("User not logged in")
        }
    }

    private fun createPurchase(
        userId: String,
        giftIds: List<String>,
        gifts: List<Gift>,
        basketDoc: QueryDocumentSnapshot
    ) {
        val total = gifts.sumOf { it.price }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val purchase = Purchase(
            status = "Processing",
            orderNumber = "ORD-${System.currentTimeMillis()}",
            quantity = giftIds.size,
            orderDate = dateFormat.format(Date()),
            estimatedDelivery = getDeliveryDate(7), // 7 días de entrega
            totalCost = total,
            giftIds = giftIds,
            userId = userId
        )

        // Guardar purchase
        purchaseDao.savePurchase(purchase,
            onSuccess = {
                // Vaciar canasta
                basketDoc.reference.update("gift_ids", emptyList<String>())
                    .addOnSuccessListener {
                        redirectToConfirmation()
                    }
            },
            onFailure = {
                showError("Error saving purchase")
            }
        )
    }

    private fun getDeliveryDate(daysToAdd: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
    }

    private fun validateForm(): Boolean {
        val fields = listOf(
            findViewById<TextView>(R.id.edit_cardholder_name),
            findViewById<TextView>(R.id.edit_card_number),
            findViewById<TextView>(R.id.edit_cvv),
            findViewById<TextView>(R.id.edit_exp_month),
            findViewById<TextView>(R.id.edit_exp_year),
            findViewById<TextView>(R.id.editTextBilling),
            findViewById<TextView>(R.id.editTextPhone)
        )

        fields.forEach { field ->
            if (field.text.isNullOrEmpty()) {
                showError("All fields are required")
                return false
            }
        }
        return true
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun redirectToConfirmation() {
        val intent = Intent(this, BasketCardConfirmationActivity::class.java)
        startActivity(intent)
        finish()
    }
}