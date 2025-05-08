package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SendInviteActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_invite)

        val cancelButton: TextView = findViewById(R.id.cancel_button)
        val whatsappShare: ImageView = findViewById(R.id.whatsapp_share)
        val facebookShare: ImageView = findViewById(R.id.facebook_share)
        val instagramShare: ImageView = findViewById(R.id.instagram_share)
        val messengerShare: ImageView = findViewById(R.id.messenger_share)
        val copyLink: ImageView = findViewById(R.id.copy_link)

        setupBottomNavigation()

        cancelButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        whatsappShare.setOnClickListener {
            shareLink(getShareText(), "com.whatsapp")
        }

        facebookShare.setOnClickListener {
            shareLink(getShareText(), "com.facebook.katana")
        }

        instagramShare.setOnClickListener {
            shareLink(getShareText(), "com.instagram.android")
        }

        messengerShare.setOnClickListener {
            shareLink(getShareText(), "com.facebook.orca")
        }

        copyLink.setOnClickListener { copyLinkToClipboard(getShareText()) }
    }

    private fun getShareText(): String {
        return "Check out Wrapsy! https://asielblox.linkmock/wrapsy"
    }

    private fun shareLink(text: String, packageName: String? = null) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)

        if (packageName != null) {
            intent.setPackage(packageName)

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                val chooserIntent = Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    },
                    "Share via"
                )
                startActivity(chooserIntent)
            }
        } else {
            val chooserIntent = Intent.createChooser(intent, "Share via")
            startActivity(chooserIntent)
        }
    }

    private fun copyLinkToClipboard(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Wrapsy Invite Link", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}