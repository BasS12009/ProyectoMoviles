package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GiftQuizStep2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_quiz_step2)

        val btn_next: Button = findViewById<Button>(R.id.btnNext2_3)
        val btn_back: Button = findViewById<Button>(R.id.btnBack)

        btn_next.setOnClickListener(){
            val intent = Intent(this, GiftQuizStep3Activity::class.java)
            startActivity(intent)
        }

        btn_back.setOnClickListener(){
            val intent = Intent(this, GiftQuizStep1Activity::class.java)
            startActivity(intent)
        }

    }
}