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
import com.google.firebase.database.FirebaseDatabase

class ProfileNewList : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_new_list)
        setupBottomNavigation()

        val btnConfirm = findViewById<Button>(R.id.btn_confirm)
        val nameInput = findViewById<EditText>(R.id.et_list_name)

        btnConfirm.setOnClickListener {
//            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
//            val db = FirebaseDatabase.getInstance().getReference("lists").child(userId)
//            val key = db.push().key ?: return@setOnClickListener
//            val list =
//                mapOf("id" to key, "name" to name, "products" to emptyList<Map<String, Any>>())
//
//            db.child(key).setValue(list).addOnSuccessListener {
//                Toast.makeText(this, "List saved", Toast.LENGTH_SHORT).show()
//                finish()
//            }.addOnFailureListener {
//                Toast.makeText(this, "Error saving", Toast.LENGTH_SHORT).show()
//            }
        }}
}