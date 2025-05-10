package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BasketCardConfirmationActivity : BaseActivity() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket_card_confirmation)

        setupBottomNavigation()

        val cardholder = findViewById<EditText>(R.id.editTextCardholderName)
        val cardNumber = findViewById<EditText>(R.id.et_card_number)
        val cvv = findViewById<EditText>(R.id.et_cvv)
        val mm = findViewById<EditText>(R.id.edit_exp_month)
        val yyyy = findViewById<EditText>(R.id.edit_exp_year)
        val billing = findViewById<EditText>(R.id.editTextBillingAddress)
        val phone = findViewById<EditText>(R.id.editTextPhone)
        val confirmButton = findViewById<TextView>(R.id.confirm_button)

        confirmButton.setOnClickListener {
            val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val cardData = mapOf(
                "cardholderName" to cardholder.text.toString(),
                "cardNumber" to cardNumber.text.toString(),
                "cvv" to cvv.text.toString(),
                "expirationMonth" to mm.text.toString(),
                "expirationYear" to yyyy.text.toString(),
                "billingAddress" to billing.text.toString(),
                "phoneNumber" to phone.text.toString(),
                "date" to currentDate
            )

            FirebaseDatabase.getInstance().reference
                .child("giftCards")
                .child(userId)
                .push()
                .setValue(cardData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Card saved!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, PurchaseHistory::class.java))
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save card", Toast.LENGTH_SHORT).show()
                }
        }
    }
}