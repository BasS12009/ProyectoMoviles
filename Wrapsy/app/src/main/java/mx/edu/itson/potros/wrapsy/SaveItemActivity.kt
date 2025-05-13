package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import mx.edu.itson.potros.wrapsy.DAOs.GiftListDAO
import mx.edu.itson.potros.wrapsy.DAOs.GiftsDAO
import mx.edu.itson.potros.wrapsy.DAOs.WishlistDAO
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.Entities.GiftList

class SaveItemActivity : BaseActivity() {
    private lateinit var wishlistDAO: WishlistDAO
    private var giftId: String = ""
    private lateinit var currentGift: Gift
    private val giftsDAO = GiftsDAO()
    private var currentWishlistId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_item)
        Log.d("SaveItemActivity", "Actividad creada")

        wishlistDAO = WishlistDAO()

        giftId = intent.getStringExtra("GIFT_ID") ?: run {
            Log.e("SaveItemActivity", "ID de regalo no proporcionado")
            Toast.makeText(this, "Error: Regalo no especificado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadGiftDetails()
        setupBottomNavigation()
        setupUI()
    }

    private fun loadGiftDetails() {
        giftsDAO.getGift(giftId) { gift ->
            if (gift != null) {
                currentGift = gift
                Log.d("SaveItemActivity", "Regalo cargado: ${gift.name}")
            } else {
                Toast.makeText(this, "Error: No se pudo cargar el regalo", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupUI() {
        findViewById<Button>(R.id.btn_wishlist)?.setOnClickListener {
            saveToWishlist()
        }

        findViewById<Button>(R.id.btn_list)?.setOnClickListener {
            Intent(this, SaveItemMyListsActivity::class.java).apply {
                putExtra("GIFT_ID", giftId)
                startActivity(this)
            }
        }
    }

    private fun saveToWishlist() {
        val userId = getCurrentUserId().takeIf { it.isNotEmpty() } ?: run {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val wishlist = wishlistDAO.getOrCreateWishlist(userId)

                if (wishlist == null) {
                    Toast.makeText(this@SaveItemActivity,
                        "Error getting or creating the wishlist",
                        Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (wishlist.id.isEmpty()) {
                    Toast.makeText(this@SaveItemActivity,
                        "Internal error with the wishlist ID",
                        Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val success = wishlistDAO.addGiftToWishlist(wishlist.id, currentGift)

                if (success) {
                    Toast.makeText(this@SaveItemActivity,
                        "¡Regalo guardado en Wishlist!",
                        Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@SaveItemActivity,
                        "Error al guardar en Wishlist",
                        Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SaveItemActivity,
                    "Unexpected error: ${e.localizedMessage}",
                    Toast.LENGTH_LONG).show()
                Log.e("SaveItemActivity", "Error getting or creating wishlist: ${e.message}", e)
            }
        }
    }

    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }
}