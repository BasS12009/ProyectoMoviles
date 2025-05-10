package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : BaseActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupBottomNavigation()
        setSelectedItem(R.id.nav_profile)

        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        val tvUsername: TextView = findViewById(R.id.tv_username)
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        currentUser?.uid?.let { userId ->
            usersRef.child(userId).child("username")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val usernameFromDB = snapshot.getValue(String::class.java)
                        if (!usernameFromDB.isNullOrEmpty()) {
                            tvUsername.text = usernameFromDB
                        } else {
                            tvUsername.text = currentUser.email ?: "Usuario"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("DB_ERROR", "Error reading username from DB", error.toException())
                        tvUsername.text = currentUser.email ?: "Error al cargar nombre"
                    }
                })
        } ?: run {
            tvUsername.text = "Invitado"
        }


        val btnMoreOptions: ImageView = findViewById(R.id.more_options)
        val btnNotification: ImageView = findViewById(R.id.btn_notificaciones)
        val btnEditar: ImageView = findViewById(R.id.iv_edit_name)
        val btnShare: ImageView = findViewById(R.id.iv_share)
        val btnLists: ImageView = findViewById(R.id.iv_lists)

        btnLists.setOnClickListener(){
            val intent = Intent(this, Profilelists::class.java)
            startActivity(intent)
        }
        btnShare.setOnClickListener(){
            val intent = Intent(this, ShareWithFriendsActivity::class.java)
            startActivity(intent)
        }

        btnMoreOptions.setOnClickListener(){
            val intent = Intent(this, MoreOptions::class.java)
            startActivity(intent)

        }

        btnNotification.setOnClickListener(){
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        btnEditar.setOnClickListener(){
            val intent = Intent(this, ProfileEditProfile::class.java)
            startActivity(intent)
        }

    }
}