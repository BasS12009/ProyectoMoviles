package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BasketCardInformationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_basket_card_information)

        setupBottomNavigation()
        val btnConfirmButton: TextView = findViewById(R.id.btn_confirm)

        btnConfirmButton.setOnClickListener(){
            val intent = Intent(this, StoresActivity::class.java)
            startActivity(intent)

        }


    }
}