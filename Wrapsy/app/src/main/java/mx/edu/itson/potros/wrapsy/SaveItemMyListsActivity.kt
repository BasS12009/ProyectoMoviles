package mx.edu.itson.potros.wrapsy

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import mx.edu.itson.potros.wrapsy.DAOs.GiftListDAO
import mx.edu.itson.potros.wrapsy.DAOs.GiftsDAO
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.Entities.GiftList

class SaveItemMyListsActivity : BaseActivity()  {
    private lateinit var giftListDAO: GiftListDAO
    private lateinit var giftsDAO: GiftsDAO
    private var giftId: String = ""
    private lateinit var currentGift: Gift
    private lateinit var buttonsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_item_my_lists)

        Log.d("SaveItemMyLists", "onCreate() - Iniciando actividad")

        giftListDAO = GiftListDAO()
        giftsDAO = GiftsDAO()

        buttonsContainer = findViewById(R.id.buttons_container)

        setupBottomNavigation()

        giftId = intent.getStringExtra("GIFT_ID") ?: ""
        if (giftId.isEmpty()) {
            Toast.makeText(this, "Error: No se especificó ningún regalo", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadGiftDetails()
    }

    private fun loadGiftDetails() {
        Log.d("SaveItemMyLists", "Cargando detalles del regalo ID: $giftId")
        giftsDAO.getGift(giftId) { gift ->
            if (gift != null) {
                currentGift = gift
                Log.d("SaveItemMyListsActivity", "Regalo cargado: ${gift.name}")
                loadUserGiftLists()
            } else {
                Toast.makeText(this, "Error: No se pudo cargar el regalo", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun loadUserGiftLists() {
        val userId = getCurrentUserId()
        if (userId.isEmpty()) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                Log.d("SaveItemMyLists", "Iniciando consulta a Firestore...")
                val giftLists = giftListDAO.getAllGiftListsByUserId(userId)
                Log.d("SaveItemMyLists", "Número de listas obtenidas: ${giftLists.size}")

                removeAllButtons()

                if (giftLists.isEmpty()) {
                    Log.d("SaveItemMyLists", "No hay listas, mostrando mensaje...")
                    val noListsMessage = TextView(this@SaveItemMyListsActivity).apply {
                        text = "No tienes listas creadas"
                        setTextColor(Color.WHITE)
                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                        setPadding(16, 16, 16, 16)
                    }
                    buttonsContainer.addView(noListsMessage)
                } else {
                    Log.d("SaveItemMyLists", "Agregando botones para ${giftLists.size} listas...")
                    for (giftList in giftLists) {
                        addButtonForGiftList(giftList)
                    }
                }

            } catch (e: Exception) {
                Log.e("SaveItemMyLists", "Error crítico: ${e.javaClass.simpleName}", e)
                Toast.makeText(this@SaveItemMyListsActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addButtonForGiftList(giftList: GiftList) {
        Log.d("SaveItemMyLists", "Creando botón para lista: ${giftList.name}")
        val button = Button(this).apply {
            text = giftList.name
            setBackgroundResource(R.drawable.purple_button_background)
            setTextColor(Color.WHITE)

            setOnClickListener {
                Log.d("SaveItemMyLists", "Botón clickeado: ${giftList.name}")
                addGiftToSelectedList(giftList)
            }
        }
        buttonsContainer.addView(button)
    }


    private fun removeAllButtons() {
        buttonsContainer.removeAllViews()
    }



    private fun addGiftToSelectedList(giftList: GiftList) {
        if (!::currentGift.isInitialized) {
            Toast.makeText(this, "Error: Regalo no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                giftListDAO.addGiftToGiftList(giftList.id, currentGift)
                Toast.makeText(this@SaveItemMyListsActivity, "¡Regalo guardado en ${giftList.name}!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Log.e("SaveItemMyListsActivity", "Error al guardar regalo en lista", e)
                Toast.makeText(this@SaveItemMyListsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getCurrentUserId(): String {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""
    }
    }