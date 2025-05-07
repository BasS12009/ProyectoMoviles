package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MoreOptions : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_options)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupBottomNavigation()

        val btnPurchaseHistory:Button = findViewById(R.id.btn_purchase_history)

        val btnNotification: ImageView = findViewById(R.id.btn_notificaciones)
        val btnPaymentOptions:Button = findViewById(R.id.btn_payment_options)
        val aboutUs:Button = findViewById(R.id.btn_about_us)
        val btnLogout: Button = findViewById(R.id.btn_logout)

        btnNotification.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        btnPurchaseHistory.setOnClickListener {
            val intent = Intent(this, PurchaseHistory::class.java)
            startActivity(intent)
        }

        btnPaymentOptions.setOnClickListener {
            val intent = Intent(this, PaymentOptions::class.java)
            startActivity(intent)
        }

        aboutUs.setOnClickListener {
            val intent = Intent(this, AboutUs::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}