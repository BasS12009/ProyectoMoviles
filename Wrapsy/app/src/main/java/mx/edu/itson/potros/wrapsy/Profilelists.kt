package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class Profilelists : BaseActivity() {
    private lateinit var db: DatabaseReference
    private lateinit var listsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profilelists)
        setupBottomNavigation()

        listsContainer = findViewById(R.id.lists_container)
        db = FirebaseDatabase.getInstance().getReference("lists")

        loadLists()
    }

    private fun loadLists() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db = FirebaseDatabase.getInstance().getReference("lists").child(userId)

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listsContainer.removeAllViews()
                for (child in snapshot.children) {
                    val name = child.child("name").getValue(String::class.java) ?: continue
                    val listId = child.key ?: continue

                    val textView = TextView(this@Profilelists)
                    textView.text = name
                    textView.textSize = 18f
                    textView.setPadding(16, 16, 16, 16)
                    textView.setOnClickListener {
                        val intent = Intent(this@Profilelists, ProfileListInfoActivity::class.java)
                        intent.putExtra("listId", listId)
                        startActivity(intent)
                    }

                    listsContainer.addView(textView)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}