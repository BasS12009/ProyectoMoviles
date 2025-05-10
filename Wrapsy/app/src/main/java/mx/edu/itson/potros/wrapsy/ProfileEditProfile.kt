package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileEditProfile : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private lateinit var databaseRef: DatabaseReference

    private lateinit var profileImage: ImageView
    private lateinit var usernameEditText: EditText
    private lateinit var btnSave: Button
    private lateinit var tvEmailDisplay: TextView

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_profile)

        setupBottomNavigation()

        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        databaseRef = FirebaseDatabase.getInstance().reference

        profileImage = findViewById(R.id.profile_image)
        usernameEditText = findViewById(R.id.username_edittext)
        btnSave = findViewById(R.id.btn_logout)
        tvEmailDisplay = findViewById(R.id.tv_email_display)

        // Mostrar el correo electrónico del usuario actual
        loadCurrentUserEmail()

        // Cargar el nombre de usuario actual al abrir la pantalla
        loadCurrentUserProfile()


        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1000)
        }

        btnSave.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val uid = auth.currentUser?.uid

            if (uid == null || username.isEmpty()) {
                Toast.makeText(this, "Faltan datos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri != null) {
                val imageRef = storageRef.child("profile_pictures/$uid.jpg")
                imageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val userProfile = mapOf(
                                "username" to username,
                                "profileImageUrl" to uri.toString()
                            )
                            databaseRef.child("users").child(uid).setValue(userProfile)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Solo actualizar el username si no se cambió imagen
                databaseRef.child("users").child(uid).child("username").setValue(username)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun loadCurrentUserEmail() {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            tvEmailDisplay.text = currentUser.email
        } else {
            tvEmailDisplay.text = "No se pudo cargar el correo"
        }
    }

    private fun loadCurrentUserProfile() {
        val uid = auth.currentUser?.uid
        uid?.let {
            databaseRef.child("users").child(it).child("username")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentUsername = snapshot.getValue(String::class.java)
                        usernameEditText.setText(currentUsername)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("DB_ERROR", "Error al cargar el nombre de usuario", error.toException())
                        Toast.makeText(this@ProfileEditProfile, "Error al cargar el nombre", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            profileImage.setImageURI(selectedImageUri) // vista previa
        }
    }
}