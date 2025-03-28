package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileEditProfile : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnNotification: ImageView = findViewById(R.id.btn_notificaciones)
        val btnMoreOption:ImageView = findViewById(R.id.more_options)


        btnNotification.setOnClickListener(){
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)

        }
        btnMoreOption.setOnClickListener(){
            val intent = Intent(this, MoreOptions::class.java)
            startActivity(intent)

        }
    }
}