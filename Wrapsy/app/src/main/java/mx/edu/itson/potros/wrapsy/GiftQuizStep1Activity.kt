package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GiftQuizStep1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_quiz_step1)

        val btnCancel: Button = findViewById(R.id.btnCancel)
        val btn_next:Button = findViewById<Button>(R.id.btnNext1_3)

        btnCancel.setOnClickListener(){
            val intent = Intent(this, StoresActivity::class.java)
            startActivity(intent)
        }
        btn_next.setOnClickListener(){
            val intent = Intent(this, GiftQuizStep2Activity::class.java)
            startActivity(intent)
        }

    }
}