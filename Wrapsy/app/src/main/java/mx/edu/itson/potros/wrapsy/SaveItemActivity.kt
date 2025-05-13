package mx.edu.itson.potros.wrapsy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SaveItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_item)

        val wishlist:Button = findViewById<Button>(R.id.btn_wishlist)
        val myList:Button = findViewById<Button>(R.id.btn_list)

        wishlist.setOnClickListener(){
            val intent = Intent(this, RecommendedGiftsActivity::class.java)
            startActivity(intent)
        }

        myList.setOnClickListener(){
            val intent = Intent(this, SaveItemMyListsActivity::class.java)
            startActivity(intent)
        }

    }
}