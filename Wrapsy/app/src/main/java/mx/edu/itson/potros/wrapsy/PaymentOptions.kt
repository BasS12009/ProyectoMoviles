package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException

class PaymentOptions : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_more_optionspayment_options)
        setupBottomNavigation()


        val btnAddCard = findViewById<Button>(R.id.btn_add_card)
        btnAddCard.setOnClickListener {
            val intent = Intent(this, CardInformationAddCardActivity::class.java)
            startActivity(intent)
        }


        val btnNotification = findViewById<ImageView>(R.id.btn_notificaciones)
        btnNotification.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }


        val selectedJson = intent.getStringExtra("selectedProducts")
        if (selectedJson != null) {
            try {
                val selectedProducts = JSONArray(selectedJson)
                val totalPrice = calculateTotal(selectedProducts)


                Toast.makeText(
                    this,
                    "Selected ${selectedProducts.length()} products. Total: $$totalPrice",
                    Toast.LENGTH_LONG
                ).show()

                Log.d("PaymentOptions", "Selected products: $selectedProducts")
                Log.d("PaymentOptions", "Total price: $$totalPrice")



            } catch (e: JSONException) {
                Log.e("PaymentOptions", "Error parsing JSON data", e)
                Toast.makeText(this, "Error loading selected products", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("PaymentOptions", "No products were selected")
            Toast.makeText(this, "No products were selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateTotal(products: JSONArray): Double {
        var total = 0.0

        for (i in 0 until products.length()) {
            try {
                val product = products.getJSONObject(i)
                when {
                    product.has("price") -> {
                        val price = when (val priceValue = product.get("price")) {
                            is Int -> priceValue.toDouble()
                            is Double -> priceValue
                            is String -> priceValue.toString().toDoubleOrNull() ?: 0.0
                            else -> 0.0
                        }
                        total += price
                    }
                }
            } catch (e: Exception) {
                Log.e("PaymentOptions", "Error calculating price for product at index $i", e)
            }
        }

        return total
    }


}