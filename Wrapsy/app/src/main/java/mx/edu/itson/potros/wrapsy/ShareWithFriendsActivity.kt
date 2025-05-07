package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ShareWithFriendsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_with_friends)

        val cancelButton: TextView  = findViewById(R.id.cancel_button)
        val sendInvitation: TextView = findViewById(R.id.tvInviteApp)

        setupBottomNavigation()

        cancelButton.setOnClickListener(){
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        sendInvitation.setOnClickListener(){
            val intent = Intent(this, SendInviteActivity::class.java)
            startActivity(intent)
        }

    }
}