package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class CardInformationAddCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_information_add_card)

        val btnBasket:ImageView = findViewById(R.id.btn_basket)
        val btnNotification:ImageView = findViewById(R.id.btn_notificaciones)
        val btnMoreOption:ImageView = findViewById(R.id.more_options)

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

        val btnConfirm:Button = findViewById<Button>(R.id.btn_confirm)



        btnConfirm.setOnClickListener(){
            val intent = Intent(this, MoreOptionspaymentOptions::class.java)
            startActivity(intent)

        }
    }
}