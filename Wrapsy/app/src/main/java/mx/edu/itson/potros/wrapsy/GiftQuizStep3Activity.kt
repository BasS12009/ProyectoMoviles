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

class GiftQuizStep3Activity : AppCompatActivity() {
    private var selectedBudgetButton: Button? = null // Para rastrear el botón de presupuesto seleccionado
    private var selectedWrappingButton: Button? = null // Para rastrear el botón de envoltorio seleccionado
    private var selectedBudget = ""
    private var selectedWrapping = ""
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_quiz_step3)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        val btnNext3_3 = findViewById<Button>(R.id.btnNext3_3)
        val btnBack = findViewById<Button>(R.id.btnBack)
        val btnPreference1 = findViewById<Button>(R.id.btnPreference1)
        val btnPreference2 = findViewById<Button>(R.id.btnPreference2)
        val btnPreference3 = findViewById<Button>(R.id.btnPreference3)
        val btnBag = findViewById<Button>(R.id.btnBag)
        val btnBox = findViewById<Button>(R.id.btnBox)
        val btnGiftWrap = findViewById<Button>(R.id.btnGiftWrap)
        val btnNoWrapping = findViewById<Button>(R.id.btnNoWrapping)

        val budgetButtons = listOf(btnPreference1, btnPreference2, btnPreference3)
        val wrappingButtons = listOf(btnBag, btnBox, btnGiftWrap, btnNoWrapping)

        budgetButtons.forEach { button ->
            button.setOnClickListener {
                selectedBudgetButton?.apply {
                    animate().alpha(1.0f).setDuration(200).start()
                }
                button.animate().alpha(0.7f).setDuration(200).start()
                selectedBudgetButton = button
                selectedBudget = button.text.toString()
            }
        }

        wrappingButtons.forEach { button ->
            button.setOnClickListener {
                selectedWrappingButton?.apply {
                    animate().alpha(1.0f).setDuration(200).start()
                }
                button.animate().alpha(0.7f).setDuration(200).start()
                selectedWrappingButton = button
                selectedWrapping = button.text.toString()
            }
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, GiftQuizStep2Activity::class.java)
            startActivity(intent)
        }

        btnNext3_3.setOnClickListener {
            if (selectedBudget.isEmpty() || selectedWrapping.isEmpty()) {
                Toast.makeText(this, "Please select budget and wrapping", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveQuizData("step3_budget", selectedBudget)
            saveQuizData("step3_wrapping", selectedWrapping)
            val intent = Intent(this, GiftQuizEndActivity::class.java)
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
                        Toast.makeText(this@GiftQuizStep3Activity, "Failed to save data. Please check your connection.", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("Firebase", "Data saved successfully!")
                    }
                }
        } else {
            Log.e("Firebase", "User not logged in")
            Toast.makeText(this@GiftQuizStep3Activity, "User not logged in. Please sign in.", Toast.LENGTH_SHORT).show()
        }
    }
}