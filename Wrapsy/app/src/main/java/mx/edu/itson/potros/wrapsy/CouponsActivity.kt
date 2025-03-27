package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView

class CouponsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupons)

        setupBottomNavigation()
        setSelectedItem(R.id.nav_coupons)

        val btnCanasta:ImageView = findViewById(R.id.basketIcon)
        val btnMoreOptions:ImageView = findViewById(R.id.more_options)

        btnCanasta.setOnClickListener(){
            val intent = Intent(this, BasketActivity::class.java)
            startActivity(intent)

        }

        btnMoreOptions.setOnClickListener(){
            val intent = Intent(this, MoreOptions::class.java)
            startActivity(intent)

        }


    }
}