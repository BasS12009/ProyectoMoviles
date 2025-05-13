package mx.edu.itson.potros.wrapsy

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SaveItemActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var saveButton: Button

    // Simulamos los datos del producto actual
    private val productId = "prod123"
    private val productName = "Coca-Cola 600ml"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_item)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        saveButton = findViewById(R.id.btn_confirm)

        saveButton.setOnClickListener {
            showListSelectionDialog()
        }
    }

    private fun showListSelectionDialog() {
        val currentUser = auth.currentUser ?: return

        val userListsRef = database.child("lists").child(currentUser.uid)

        userListsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listNames = mutableListOf<String>()
                val listIds = mutableListOf<String>()

                for (listSnapshot in snapshot.children) {
                    val name = listSnapshot.child("name").getValue(String::class.java)
                    if (!name.isNullOrEmpty()) {
                        listNames.add(name)
                        listIds.add(listSnapshot.key ?: "")
                    }
                }

                if (listNames.isEmpty()) {
                    Toast.makeText(this@SaveItemActivity, "No tienes listas creadas", Toast.LENGTH_SHORT).show()
                    return
                }

                AlertDialog.Builder(this@SaveItemActivity)
                    .setTitle("Selecciona una lista")
                    .setItems(listNames.toTypedArray()) { _, which ->
                        val selectedListId = listIds[which]
                        saveItemToList(currentUser.uid, selectedListId)
                    }
                    .show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SaveItemActivity, "Error al cargar listas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveItemToList(userId: String, listId: String) {
        val itemData = mapOf(
            "id" to productId,
            "name" to productName
        )

        val itemsRef = database.child("lists").child(userId).child(listId).child("items").push()
        itemsRef.setValue(itemData)
            .addOnSuccessListener {
                Toast.makeText(this, "Producto guardado en la lista", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar el producto", Toast.LENGTH_SHORT).show()
            }
    }
}