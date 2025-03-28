package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SingUpAdressActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up_adress)

        val btnReady: Button = findViewById(R.id.btn_ready)

        btnReady.setOnClickListener(){
            val intent = Intent(this, PerfectGiftQuizActivity::class.java)
            startActivity(intent)
        }
    }
}