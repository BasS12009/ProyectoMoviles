package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SingUpActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        auth = Firebase.auth

        val nombreEditText: EditText = findViewById(R.id.etNombre)
        val usuarioEditText: EditText = findViewById(R.id.etUser)
        val correoEditText: EditText = findViewById(R.id.etCorreo)
        val numeroEditText: EditText = findViewById(R.id.etNumero)
        val contrasenaEditText: EditText = findViewById(R.id.etContrasena)
        val confirmarContrasenaEditText: EditText = findViewById(R.id.etConfirmarContrasena)

        val btnConfirm: Button = findViewById(R.id.btn_confirm)
        val btnLogin: TextView = findViewById(R.id.login_link)

        database = FirebaseDatabase.getInstance().getReference("users")

        btnConfirm.setOnClickListener(){
            registerUser(nombreEditText, usuarioEditText, correoEditText, numeroEditText, contrasenaEditText, confirmarContrasenaEditText)
        }

        btnLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser(
        nombreEditText: EditText,
        usuarioEditText: EditText,
        correoEditText: EditText,
        numeroEditText: EditText,
        contrasenaEditText: EditText,
        confirmarContrasenaEditText: EditText
    ) {
        val nombre = nombreEditText.text.toString().trim()
        val usuario = usuarioEditText.text.toString().trim()
        val correo = correoEditText.text.toString().trim()
        val numero = numeroEditText.text.toString().trim()
        val contrasena = contrasenaEditText.text.toString()
        val confirmarContrasena = confirmarContrasenaEditText.text.toString()

        if (nombre.isEmpty() || usuario.isEmpty() || correo.isEmpty() || numero.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (contrasena != confirmarContrasena) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // El registro con Firebase Authentication fue exitoso
                    val user = auth.currentUser
                    val userId = user?.uid

                    if (userId != null) {
                        val userObj = User(userId, nombre, usuario, correo, numero)
                        database.child(userId).setValue(userObj)
                            .addOnSuccessListener {
                                val intent = Intent(this, SingUpAdressActivity::class.java)
                                intent.putExtra("USER_ID", userId)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Error al registrar el usuario: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                user?.delete()?.addOnSuccessListener { /* Usuario de Auth eliminado */ }
                                    ?.addOnFailureListener { /* No se pudo eliminar usuario de Auth */ }
                            }
                    }
                } else {
                    // Si falla el registro con Firebase Authentication, muestra un mensaje
                    Toast.makeText(
                        this,
                        "Error al registrar usuario: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}