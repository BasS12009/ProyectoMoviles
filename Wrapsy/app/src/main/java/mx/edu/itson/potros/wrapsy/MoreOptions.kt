package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView

class MoreOptions : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_options)

        setupBottomNavigation()

        val btnNotification: ImageView = findViewById(R.id.btn_notificaciones)

        btnNotification.setOnClickListener() {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)

        }
    }
}