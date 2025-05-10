package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.core.content.ContextCompat

class PurchaseHistory : BaseActivity() {
    private lateinit var giftCardContainer: LinearLayout
    private lateinit var databaseRef: DatabaseReference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_optionspurchase_history)

        giftCardContainer = findViewById(R.id.giftCardContainer)
        databaseRef = FirebaseDatabase.getInstance().reference.child("giftCards").child(userId)

        loadGiftCards()
        setupBottomNavigation()
    }

    private fun loadGiftCards() {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                giftCardContainer.removeAllViews()
                for (cardSnapshot in snapshot.children) {
                    val name = cardSnapshot.child("cardholderName").getValue(String::class.java) ?: "Unknown"
                    val date = cardSnapshot.child("date").getValue(String::class.java) ?: "N/A"
                    val number = cardSnapshot.child("cardNumber").getValue(String::class.java) ?: "0000"
                    val lastDigits = number.takeLast(4)

                    val button = Button(this@PurchaseHistory).apply {
                        text = "Gift for $name - $date\nCard ending in - $lastDigits"
                        setPadding(16, 16, 16, 16)
                        setTextColor(ContextCompat.getColor(context, android.R.color.black))
                        background = ContextCompat.getDrawable(context, R.drawable.edit_text_bg)
                    }

                    giftCardContainer.addView(button)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}