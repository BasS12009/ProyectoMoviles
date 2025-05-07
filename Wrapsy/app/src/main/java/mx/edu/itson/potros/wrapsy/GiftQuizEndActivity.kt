package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GiftQuizEndActivity : AppCompatActivity() {
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_quiz) // Asegúrate de que este es el layout correcto

        mDatabase = FirebaseDatabase.getInstance().getReference()

        val btnAccept = findViewById<Button>(R.id.btnAccept)

        btnAccept.setOnClickListener {
            retrieveQuizData()
            val intent = Intent(this, StoresActivity::class.java)
            startActivity(intent)
        }
    }

    private fun retrieveQuizData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            mDatabase.child("users").child(userId).child("quiz")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Aquí obtienes los datos del quiz
                        val step1 = dataSnapshot.child("step1").getValue(String::class.java)
                        val step2Occasion = dataSnapshot.child("step2_occasion").getValue(String::class.java)
                        val step2Color = dataSnapshot.child("step2_color").getValue(String::class.java)
                        val step3Budget = dataSnapshot.child("step3_budget").getValue(String::class.java)
                        val step3Wrapping = dataSnapshot.child("step3_wrapping").getValue(String::class.java)

                        // Muestra los datos en un Toast
                        val message = "Your preferences have been saved"
                        Toast.makeText(this@GiftQuizEndActivity, message, Toast.LENGTH_LONG).show()

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("Firebase", "Error retrieving quiz data: " + databaseError.message)
                        Toast.makeText(this@GiftQuizEndActivity, "Failed to retrieve quiz data.", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Log.e("Firebase", "User not logged in")
            Toast.makeText(this@GiftQuizEndActivity, "User not logged in. Please sign in.", Toast.LENGTH_SHORT).show()
        }
    }
}