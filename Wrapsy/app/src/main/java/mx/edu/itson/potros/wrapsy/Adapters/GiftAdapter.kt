package mx.edu.itson.potros.wrapsy.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.R

class GiftAdapter(private val context: Context, private val gifts: MutableList<Gift>) :
    ArrayAdapter<Gift>(context, 0, gifts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_gift, parent, false)
        }

        val gift = gifts[position]

        // Set up the views for each gift item
        val imageView = itemView?.findViewById<ImageView>(R.id.image_producto_cell)
        val nameTextView = itemView?.findViewById<TextView>(R.id.titulo_producto_cell)

        nameTextView?.text = gift.name

        // Load image using Glide or similar library
        if (gift.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(gift.imageUrl)
                .into(imageView!!)
        } else if (gift.imageResourceId != 0) {
            imageView?.setImageResource(gift.imageResourceId)
        }

        return itemView!!
    }
}