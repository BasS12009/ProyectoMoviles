package mx.edu.itson.potros.wrapsy.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.R

class CategoryGiftAdapter(
    private val context: Context,
    private val gifts: MutableList<Gift>
) : ArrayAdapter<Gift>(context, 0, gifts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_gift, parent, false)

        val gift = getItem(position)!!

        val imageView = itemView.findViewById<ImageView>(R.id.image_producto_cell)
        val title = itemView.findViewById<TextView>(R.id.titulo_producto_cell)
        val description = itemView.findViewById<TextView>(R.id.descripcion_producto_cell)

        title.text = gift.name
        description.text = gift.description

        if (gift.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(gift.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        } else {
            imageView.setImageResource(gift.imageResourceId)
        }

        return itemView
    }
}