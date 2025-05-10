package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {

    // Función para configurar el BottomNavigationView
    // Hay que cambiar los Activities por los que vayan creando
    // Los activities que se agreguen aqui, tienen que heredar de esta clase
    protected fun setupBottomNavigation() {
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_stores -> {
                    if(this !is StoresActivity) {
                        startActivity(Intent(this, StoresActivity::class.java))
                    }
                    true
                }

                R.id.nav_orders -> {
                    if(this !is OrdersActivity) {
                        startActivity(Intent(this, OrdersActivity::class.java))
                    }
                    true
                }

                R.id.nav_coupons -> {
                    if(this !is CouponsActivity) {
                        startActivity(Intent(this, CouponsActivity::class.java))
                    }
                    true
                }

                R.id.nav_profile -> {
                    if(this !is ProfileActivity) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                    true
                }
                else -> false
            }
        }
    }

    // Resalta el ítem correspondiente a la actividad actual
    protected fun setSelectedItem(itemId: Int) {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = itemId;
    }

    protected fun setupTopBarNavigation() {
        val topBar = findViewById<RelativeLayout>(R.id.top_bar)
        val basketIconImageView: ImageView? = topBar?.findViewById(R.id.basketIcon)
        val menuIconImageView: ImageView? = topBar?.findViewById(R.id.menuIcon)
        val optionsImageView: ImageView? = topBar?.findViewById(R.id.options)

        basketIconImageView?.setOnClickListener {
            startActivity(Intent(this, BasketActivity::class.java))
        }

        menuIconImageView?.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        optionsImageView?.setOnClickListener {
            startActivity(Intent(this, MoreOptions::class.java))
        }
    }
}