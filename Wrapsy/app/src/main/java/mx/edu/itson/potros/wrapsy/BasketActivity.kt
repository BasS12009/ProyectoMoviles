package mx.edu.itson.potros.wrapsy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import mx.edu.itson.potros.wrapsy.Entities.Gift

class BasketActivity : BaseActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)

        val btnPayOrder: Button = findViewById(R.id.btn_pay_order)

        setupBottomNavigation()


        btnPayOrder.setOnClickListener() {
            val intent = Intent(this, ChooseCardActivity::class.java)
            startActivity(intent)
        }


    }


}