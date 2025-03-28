package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SingUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        val btnConfirm: Button = findViewById(R.id.btn_confirm)

        btnConfirm.setOnClickListener(){
            val intent = Intent(this, SingUpAdressActivity::class.java)
            startActivity(intent)
        }
    }
}