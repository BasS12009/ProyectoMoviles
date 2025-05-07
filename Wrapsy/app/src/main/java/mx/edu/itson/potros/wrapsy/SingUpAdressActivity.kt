package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SingUpAdressActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var userId: String //aquí obtenemos el user id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up_adress)

        database = FirebaseDatabase.getInstance().getReference("users")
        userId = intent.getStringExtra("USER_ID") ?: ""

        val paisEditText: EditText = findViewById(R.id.etPais)
        val estadoEditText: EditText = findViewById(R.id.etEstado)
        val ciudadEditText: EditText = findViewById(R.id.etCiudad)
        val codigoPostalEditText: EditText = findViewById(R.id.etCodigoP)
        val direccionEditText: EditText = findViewById(R.id.etDireccion)
        val btnReady: Button = findViewById(R.id.btn_ready)

        btnReady.setOnClickListener {
            val pais = paisEditText.text.toString().trim()
            val estado = estadoEditText.text.toString().trim()
            val ciudad = ciudadEditText.text.toString().trim()
            val codigoPostal = codigoPostalEditText.text.toString().trim()
            val direccion = direccionEditText.text.toString().trim()

            if (pais.isEmpty() || estado.isEmpty() || ciudad.isEmpty() || codigoPostal.isEmpty() || direccion.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos de dirección", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Usa un return etiquetado para salir del lambda
            }

            // Guarda la dirección del usuario en la base de datos, usando el userId como clave
            val address = Address(pais, estado, ciudad, codigoPostal, direccion) //creamos el objeto address
            database.child(userId).child("address").setValue(address) //aquí la guardamos dentro del nodo del usuario
                .addOnSuccessListener {
                    Toast.makeText(this, "Dirección guardada exitosamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, PerfectGiftQuizActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar la dirección: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}