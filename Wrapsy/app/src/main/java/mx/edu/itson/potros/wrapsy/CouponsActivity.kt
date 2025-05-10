package mx.edu.itson.potros.wrapsy


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CouponsActivity : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var couponAdapter: CouponAdapter
    private val coupons = mutableListOf<Coupon>()
    private val db = FirebaseFirestore.getInstance() // Instancia de Firestore
    private val couponsCollection = db.collection("coupons")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupons)


        setupBottomNavigation()
        setupViews()
        fetchCoupons()
    }

    private fun setupViews() {
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.couponsRecyclerView) // Asegúrate de tener este ID en tu layout
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Setup adapter with empty list (will be populated later)
        couponAdapter = CouponAdapter(this, coupons) { coupon ->
            // Handle coupon click
            Toast.makeText(this, "Coupon selected: ${coupon.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = couponAdapter

        // Setup navigation buttons
        val btnCanasta: ImageView = findViewById(R.id.basketIcon)
        val btnMoreOptions: ImageView = findViewById(R.id.more_options)
        val btnNotification: ImageView = findViewById(R.id.btn_notificaciones)

        btnCanasta.setOnClickListener {
            val intent = Intent(this, BasketActivity::class.java)
            startActivity(intent)
        }

        btnMoreOptions.setOnClickListener {
            val intent = Intent(this, MoreOptions::class.java)
            startActivity(intent)
        }

        btnNotification.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchCoupons() {
        couponsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                coupons.clear() // Limpiar la lista anterior
                for (document in querySnapshot) {
                    val coupon = document.toObject(Coupon::class.java)
                    coupons.add(coupon)
                }
                couponAdapter.notifyDataSetChanged() // Notificar al adapter que los datos han cambiado
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting documents.", e)
                Toast.makeText(this, "Error fetching coupons.", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "CouponsActivity"
    }
}