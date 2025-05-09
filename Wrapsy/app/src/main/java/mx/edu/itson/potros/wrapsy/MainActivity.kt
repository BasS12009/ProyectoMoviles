package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSignUp: Button = findViewById(R.id.btnSignUp)
        val btnLogin: Button = findViewById(R.id.btnLogin)



        btnSignUp.setOnClickListener(){
            val intent = Intent(this, SingUpActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
    }
}