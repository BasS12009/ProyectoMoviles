package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GiftQuizEndActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_quiz)

        val aceptar:Button = findViewById<Button>(R.id.btnAccept)

        aceptar.setOnClickListener(){
            val intent = Intent(this, GiftQuizStep1Activity::class.java)
            startActivity(intent)

        }





    }
}