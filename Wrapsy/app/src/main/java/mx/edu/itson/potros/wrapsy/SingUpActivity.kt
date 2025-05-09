package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SingUpActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val RC_SIGN_IN = 9001 // Puedes usar el mismo o uno diferente
    private val TAG = "SingUpActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference("users")

        // Configuración para el inicio de sesión con Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Asegúrate de tener esto en strings.xml
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Inicializa el ActivityResultLauncher para el inicio de sesión con Google
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                handleSignInResult(task)
            } else {
                Log.w(TAG, "Google sign in failed")
                Toast.makeText(this, "Falló el inicio de sesión con Google.", Toast.LENGTH_SHORT).show()
            }
        }

        val nombreEditText: EditText = findViewById(R.id.etNombre)
        val usuarioEditText: EditText = findViewById(R.id.etUser)
        val correoEditText: EditText = findViewById(R.id.etCorreo)
        val numeroEditText: EditText = findViewById(R.id.etNumero)
        val contrasenaEditText: EditText = findViewById(R.id.etContrasena)
        val confirmarContrasenaEditText: EditText = findViewById(R.id.etConfirmarContrasena)

        val btnConfirm: Button = findViewById(R.id.btn_confirm)
        val btnLogin: TextView = findViewById(R.id.login_link)
        val btnGoogleSignUp: Button = findViewById(R.id.google_button)

        database = FirebaseDatabase.getInstance().getReference("users")

        btnConfirm.setOnClickListener{
            registerUser(nombreEditText, usuarioEditText, correoEditText, numeroEditText, contrasenaEditText, confirmarContrasenaEditText)
        }

        btnGoogleSignUp.setOnClickListener{
            signInWithGoogle()
        }

        btnLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, "signInResult:success account=${account.email}")
            firebaseAuthWithGoogle(account.idToken)
        } catch (e: ApiException) {
            // La autenticación con Google falló
            Log.e(TAG, "signInResult:failed code=${e.statusCode}, message=${e.message}")
            Toast.makeText(this, "Falló el inicio de sesión con Google: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        if (idToken != null) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Inicio de sesión con Google en Firebase exitoso
                        val user = auth.currentUser
                        user?.let {
                            val userId = it.uid
                            val email = it.email ?: ""
                            val displayName = it.displayName ?: ""
                            val username = displayName.replace(" ", "").lowercase() // Ejemplo simple de username

                            val userObj = User(userId, displayName, username, email, "") // El número de teléfono podría estar vacío
                            database.child(userId).setValue(userObj)
                                .addOnSuccessListener {
                                    val intent = Intent(this, SingUpAdressActivity::class.java)
                                    intent.putExtra("USER_ID", userId)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error al registrar usuario en DB", e)
                                    Toast.makeText(
                                        this,
                                        "Error al registrar el usuario: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    it.delete() // Opcional: Eliminar el usuario de Auth si falla el registro en la base de datos
                                }
                        }
                    } else {
                        // Si falla la autenticación con Firebase, muestra un mensaje
                        Log.w(TAG, "signInWithCredential failed", task.exception)
                        Toast.makeText(this, "Falló la autenticación con Google en Firebase: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "No se pudo obtener el ID token de Google.", Toast.LENGTH_SHORT).show()
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