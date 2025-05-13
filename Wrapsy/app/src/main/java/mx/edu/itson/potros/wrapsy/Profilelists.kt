package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
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

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var listsContainer: LinearLayout
    private lateinit var newListButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profilelists)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        listsContainer = findViewById(R.id.lists_container)
        newListButton = findViewById(R.id.btn_new_list)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            loadUserLists(currentUser.uid)
        }

        newListButton.setOnClickListener {
            val intent = Intent(this, ProfileNewList::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserLists(userId: String) {
        val listsRef = database.child("lists").child(userId)

        listsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listsContainer.removeAllViews()

                for (listSnapshot in snapshot.children) {
                    val listName = listSnapshot.child("name").getValue(String::class.java)
                    val listId = listSnapshot.key

                    if (!listName.isNullOrEmpty() && listId != null) {
                        val listButton = Button(this@Profilelists).apply {
                            text = listName
                            setBackgroundResource(R.drawable.green_rounded_background_card)
                            setTextColor(resources.getColor(R.color.white, null))
                            textSize = 16f
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, 16, 0, 0)
                            }
                            setOnClickListener {
                                val intent = Intent(this@Profilelists, ProfileNewList::class.java)
                                intent.putExtra("LIST_ID", listId)
                                intent.putExtra("LIST_NAME", listName)
                                startActivity(intent)
                            }
                        }
                        listsContainer.addView(listButton)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}