package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PaymentOptions : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_more_optionspayment_options)
        setupBottomNavigation()

        val btnAddCard: Button = findViewById(R.id.btn_add_card)

        btnAddCard.setOnClickListener(){
            val intent = Intent(this, CardInformationAddCardActivity::class.java)
            startActivity(intent)
        }


        val btnNotification:ImageView = findViewById(R.id.btn_notificaciones)


        btnNotification.setOnClickListener(){
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)

        }


    }
}