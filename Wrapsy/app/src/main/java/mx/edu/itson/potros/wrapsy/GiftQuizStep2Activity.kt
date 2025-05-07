package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GiftQuizStep2Activity : AppCompatActivity() {
    private var selectedOccasion = ""
    private var selectedColor = ""
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_quiz_step2)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        val btnNext2_3 = findViewById<Button>(R.id.btnNext2_3)
        val btnBack = findViewById<Button>(R.id.btnBack)
        val btnOccasion1 = findViewById<Button>(R.id.btnOccasion1)
        val btnOccasion2 = findViewById<Button>(R.id.btnOccasion2)
        val btnOccasion3 = findViewById<Button>(R.id.btnOccasion3)
        val btnOccasion4 = findViewById<Button>(R.id.btnOccasion4)
        val btnBlue = findViewById<Button>(R.id.btnBlue)
        val btnRed = findViewById<Button>(R.id.btnRed)
        val btnPink = findViewById<Button>(R.id.btnPink)
        val btnOrange = findViewById<Button>(R.id.btnOrange)
        val btnPurple = findViewById<Button>(R.id.btnPurple)
        val btnBlack = findViewById<Button>(R.id.btnBlack)

        btnOccasion1.setOnClickListener {
            selectedOccasion = "Birthday"
        }

        btnOccasion2.setOnClickListener {
            selectedOccasion = "Anniversary"
        }

        btnOccasion3.setOnClickListener {
            selectedOccasion = "Holiday"
        }

        btnOccasion4.setOnClickListener {
            selectedOccasion = "No special occasion"
        }

        btnBlue.setOnClickListener {
            selectedColor = "Blue"
        }

        btnRed.setOnClickListener {
            selectedColor = "Red"
        }

        btnPink.setOnClickListener {
            selectedColor = "Pink"
        }

        btnOrange.setOnClickListener {
            selectedColor = "Orange"
        }

        btnPurple.setOnClickListener {
            selectedColor = "Purple"
        }

        btnBlack.setOnClickListener {
            selectedColor = "Black"
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, GiftQuizStep1Activity::class.java)
            startActivity(intent)
        }

        btnNext2_3.setOnClickListener {
            if (selectedOccasion.isEmpty() || selectedColor.isEmpty()) {
                Toast.makeText(this, "Please select occasion and color", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveQuizData("step2_occasion", selectedOccasion)
            saveQuizData("step2_color", selectedColor)
            val intent = Intent(this, GiftQuizStep3Activity::class.java)
            startActivity(intent)
        }
    }

    private fun saveQuizData(step: String, answer: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val quizData = HashMap<String, Any>()
            quizData[step] = answer

            mDatabase.child("users").child(userId).child("quiz")
                .updateChildren(quizData) { error, ref ->
                    if (error != null) {
                        Log.e("Firebase", "Error saving data: " + error.message)
                        Toast.makeText(this@GiftQuizStep2Activity, "Failed to save data. Please check your connection.", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("Firebase", "Data saved successfully!")
                    }
                }
        } else {
            Log.e("Firebase", "User not logged in")
            Toast.makeText(this@GiftQuizStep2Activity, "User not logged in. Please sign in.", Toast.LENGTH_SHORT).show()
        }
    }
}