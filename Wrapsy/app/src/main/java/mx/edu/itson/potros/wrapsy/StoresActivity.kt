package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StoresActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stores)

        val btnBasket: ImageView = findViewById(R.id.basketIcon)
        val btnNotification:ImageView = findViewById(R.id.btn_notificaciones)
        val btnMoreOption:ImageView = findViewById(R.id.options)

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


        val btnDetails:Button = findViewById<Button>(R.id.btn_details)

        btnDetails.setOnClickListener(){
            val intent = Intent(this, PlantillaStoreActivity::class.java)
            startActivity(intent)
        }


        setupBottomNavigation()
        setSelectedItem(R.id.nav_stores)
    }
}