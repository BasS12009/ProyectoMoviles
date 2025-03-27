package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GiftQuizStep3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_quiz_step3)

        val btn_next: Button = findViewById<Button>(R.id.btnNext3_3)

        btn_next.setOnClickListener(){
            val intent = Intent(this, GiftQuizStep3Activity::class.java)
            startActivity(intent)
        }

    }
}