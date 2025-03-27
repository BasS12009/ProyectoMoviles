package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CouponsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupons)
        setupBottomNavigation()
        setSelectedItem(R.id.nav_coupons)

        val btnCarrito:ImageView = findViewById(R.id.basketIcon)

        btnCarrito.setOnClickListener(){
            val intent = Intent(this, ::class.java)
            startActivity(intent)

        }


    }
}