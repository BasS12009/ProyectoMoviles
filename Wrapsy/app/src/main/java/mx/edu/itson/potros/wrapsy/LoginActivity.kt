package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        val btnLogin: Button = findViewById(R.id.btn_login)
        val btnSignUp: TextView = findViewById(R.id.btn_sign_up)

        btnLogin.setOnClickListener (){
            val intent = Intent(this, StoresActivity::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener(){
            val intent = Intent(this, SingUpActivity::class.java)
            startActivity(intent)
        }


    }
}