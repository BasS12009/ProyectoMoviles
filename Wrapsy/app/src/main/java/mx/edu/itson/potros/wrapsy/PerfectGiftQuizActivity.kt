package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PerfectGiftQuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfect_gift_quiz)

        val btnYes: Button = findViewById(R.id.btnYes)
        val btnSkip: Button = findViewById(R.id.btnSkip)

        btnYes.setOnClickListener(){
            val intent = Intent(this, GiftQuizStep1Activity::class.java)
            startActivity(intent)
        }

        btnSkip.setOnClickListener(){
            val intent = Intent(this, StoresActivity::class.java)
            startActivity(intent)
        }
    }
}