package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONArray

class ProfileListInfoActivity : BaseActivity() {
    private lateinit var productLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_list_info)
        setupBottomNavigation()

        productLayout = findViewById(R.id.items_container)

        val listId = intent.getStringExtra("listId") ?: return
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val db = FirebaseDatabase.getInstance().getReference("lists/$userId/$listId/products")

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the container first
                val productosCatalogo = findViewById<LinearLayout>(R.id.productos_catalogo)
                productosCatalogo.removeAllViews()

                for (productSnap in snapshot.children) {
                    val name = productSnap.child("name").getValue(String::class.java) ?: ""
                    val price = productSnap.child("price").getValue(Int::class.java) ?: 0

                    val productView = layoutInflater.inflate(R.layout.activity_producto_card, null)
                    productView.findViewById<TextView>(R.id.product_name).text = name
                    productView.findViewById<TextView>(R.id.product_price).text = "$$price"

                    productosCatalogo.addView(productView)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val btnSubmit = findViewById<TextView>(R.id.submit_button)
        btnSubmit.setOnClickListener {

            val selectedProducts = JSONArray()

            val productosCatalogo = findViewById<LinearLayout>(R.id.productos_catalogo)
            for (i in 0 until productosCatalogo.childCount) {
                val view = productosCatalogo.getChildAt(i)
                val checkBox = view.findViewById<CheckBox>(R.id.product_checkbox)

                if (checkBox != null && checkBox.isChecked) {
                    val name = view.findViewById<TextView>(R.id.product_name).text.toString()
                    val priceText = view.findViewById<TextView>(R.id.product_price).text.toString()
                    val price = priceText.replace("$", "").toIntOrNull() ?: 0


                    val productJSON = org.json.JSONObject()
                    productJSON.put("name", name)
                    productJSON.put("price", price)

                    selectedProducts.put(productJSON)
                }
            }

            val intent = Intent(this, PaymentOptions::class.java)
            intent.putExtra("selectedProducts", selectedProducts.toString())
            startActivity(intent)
        }
    }
}