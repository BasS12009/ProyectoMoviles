package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge

class BasketCardConfirmationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_basket_card_confirmation)

        setupBottomNavigation()
        setupTopBarNavigation()

        val btnConfirm = findViewById<Button>(R.id.btn_confirm_card)
        btnConfirm.setOnClickListener {
            redirectToStore()
        }
    }

    private fun redirectToStore() {
        val intent = Intent(this, StoresActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}