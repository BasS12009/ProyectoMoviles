package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OrdersActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBottomNavigation()
        setSelectedItem(R.id.nav_orders)

        val btnBasket: ImageView = findViewById(R.id.btn_basket)
        val btnNotification:ImageView = findViewById(R.id.btn_notificaciones)
        val btnMoreOption:ImageView = findViewById(R.id.more_options)
        val btnOrder:ImageView = findViewById(R.id.btn_order)

        btnOrder.setOnClickListener(){
            val intent = Intent(this, OrderDetailsActivity::class.java)
            startActivity(intent)
        }

        btnBasket.setOnClickListener(){
            val intent = Intent(this, BasketActivity::class.java)
            startActivity(intent)

        }
        btnNotification.setOnClickListener(){
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)

        }
        btnMoreOption.setOnClickListener(){
            val intent = Intent(this, MoreOptions::class.java)
            startActivity(intent)

        }
    }
}