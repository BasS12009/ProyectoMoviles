package mx.edu.itson.potros.wrapsy

import android.content.Intent
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
                        startActivity(Intent(this, PlantillaStoreActivity::class.java))
                    }
                    true
                }

                R.id.nav_orders -> {
                    if(this !is BaseActivity) {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    true
                }

                R.id.nav_coupons -> {
                    if(this !is BaseActivity) {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    true
                }

                R.id.nav_profile -> {
                    if(this !is BaseActivity) {
                        startActivity(Intent(this, MainActivity::class.java))
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
}