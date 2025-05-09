package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileEditProfile : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private lateinit var databaseRef: DatabaseReference

    private lateinit var profileImage: ImageView
    private lateinit var usernameEditText: EditText
    private lateinit var btnSave: Button

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_profile)

        setupBottomNavigation()
        setSelectedItem(R.id.nav_profile)

        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        databaseRef = FirebaseDatabase.getInstance().reference

        profileImage = findViewById(R.id.profile_image)
        usernameEditText = findViewById(R.id.username_edittext)
        btnSave = findViewById(R.id.btn_logout)


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            profileImage.setImageURI(selectedImageUri) // vista previa
        }
    }
}