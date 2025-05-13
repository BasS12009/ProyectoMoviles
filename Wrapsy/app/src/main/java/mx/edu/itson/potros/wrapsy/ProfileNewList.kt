package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileNewList : BaseActivity() {

    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var editTextListName: EditText
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_new_list)

        editTextListName = findViewById(R.id.et_list_name)
        buttonSave = findViewById(R.id.btn_confirm)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference("lists")

        buttonSave.setOnClickListener {
            saveList()
        }
    }

    private fun saveList() {
        val listName = editTextListName.text.toString().trim()
        val userId = auth.currentUser?.uid

        if (listName.isEmpty()) {
            Toast.makeText(this, "El nombre de la lista no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId != null) {
            val listId = db.child(userId).push().key

            if (listId != null) {
                val listData = mapOf(
                    "name" to listName
                )

                db.child(userId).child(listId).setValue(listData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Lista guardada exitosamente", Toast.LENGTH_SHORT).show()
                        finish() // Regresar a la pantalla anterior
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar la lista", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}