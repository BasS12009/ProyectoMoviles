package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase

class LoginActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        auth = Firebase.auth

        emailEditText = findViewById(R.id.etEmail)
        passwordEditText = findViewById(R.id.etPassword)
        val btnLogin: Button = findViewById(R.id.btn_login)
        val btnSignUp: TextView = findViewById(R.id.btn_sign_up)
        val btnGmail: Button = findViewById(R.id.btnGmail)

        btnLogin.setOnClickListener {
            loginUser()
        }

        btnSignUp.setOnClickListener {
            val intent = Intent(this, SingUpActivity::class.java)
            startActivity(intent)
        }

        btnGmail.setOnClickListener {
            // TODO: Implementar el inicio de sesión con Gmail (Firebase Authentication)
            Toast.makeText(this, "Inicio de sesión con Gmail no implementado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa tu correo electrónico y contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    val user = auth.currentUser
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, StoresActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Si falla el inicio de sesión, muestra un mensaje de error
                    Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}