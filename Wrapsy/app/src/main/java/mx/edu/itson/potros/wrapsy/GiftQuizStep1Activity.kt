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

class GiftQuizStep1Activity : AppCompatActivity() {

    private var selectedPreferenceButton: Button? = null // Para rastrear el botón seleccionado
    private var selectedPreference1 = ""
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_quiz_step1)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnNext1_3 = findViewById<Button>(R.id.btnNext1_3)
        val btnPreference1 = findViewById<Button>(R.id.btnPreference1)
        val btnPreference2 = findViewById<Button>(R.id.btnPreference2)
        val btnPreference3 = findViewById<Button>(R.id.btnPreference3)
        val btnPreference4 = findViewById<Button>(R.id.btnPreference4)

        val preferenceButtons = listOf(btnPreference1, btnPreference2, btnPreference3, btnPreference4)

        preferenceButtons.forEach { button ->
            button.setOnClickListener {
                // Deseleccionar el botón previamente seleccionado
                selectedPreferenceButton?.apply {
                    animate().alpha(1.0f).setDuration(200).start()
                }
                // Seleccionar el botón actual
                button.animate().alpha(0.7f).setDuration(200).start()
                selectedPreferenceButton = button
                selectedPreference1 = button.text.toString() // Guardar la preferencia
            }
        }


        btnCancel.setOnClickListener {
            val intent = Intent(this, StoresActivity::class.java)
            startActivity(intent)
        }

        btnNext1_3.setOnClickListener {
            if (selectedPreference1.isEmpty()) {
                Toast.makeText(this, "Please select a preference", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveQuizData("step1", selectedPreference1)
            val intent = Intent(this, GiftQuizStep2Activity::class.java)
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
                        Toast.makeText(this@GiftQuizStep1Activity, "Failed to save data. Please check your connection.", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("Firebase", "Data saved successfully!")
                    }
                }
        } else {
            Log.e("Firebase", "User not logged in")
            Toast.makeText(this@GiftQuizStep1Activity, "User not logged in. Please sign in.", Toast.LENGTH_SHORT).show()
        }
    }
}