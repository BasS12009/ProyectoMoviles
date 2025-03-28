package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupBottomNavigation()
        setSelectedItem(R.id.nav_profile)

        val btnMoreOptions: ImageView = findViewById(R.id.more_options)
        val btnNotification: ImageView = findViewById(R.id.btn_notificaciones)
        val btnEditar: ImageView = findViewById(R.id.iv_edit_name)

        btnMoreOptions.setOnClickListener(){
            val intent = Intent(this, MoreOptions::class.java)
            startActivity(intent)

        }

        btnNotification.setOnClickListener(){
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        btnEditar.setOnClickListener(){
            val intent = Intent(this, ProfileEditProfile::class.java)
            startActivity(intent)
        }

    }
}