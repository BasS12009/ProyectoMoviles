package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileNewList : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_new_list)
        setupBottomNavigation()

        val btnConfirm:Button = findViewById<Button>(R.id.btn_confirm)

        btnConfirm.setOnClickListener(){
            val intent = Intent(this, Profilelists::class.java)
            startActivity(intent)
        }
    }
}