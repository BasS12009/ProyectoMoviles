package mx.edu.itson.potros.wrapsy.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import mx.edu.itson.potros.wrapsy.BasketActivity
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.GiftDetailActivity
import mx.edu.itson.potros.wrapsy.R



class GiftBasketAdapter(
    var context: Context?,
    var gift: ArrayList<Gift>
) : BaseAdapter() {

    override fun getCount(): Int {
        return gift.size
    }

    override fun getItem(position: Int): Any {
        return gift[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var vista = convertView
        if (vista == null) {
            val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            vista = inflater.inflate(R.layout.item_basket, null)
        }

        val currentGift = getItem(position) as Gift
        val image: ImageView = vista!!.findViewById(R.id.image_producto_basket)
        val title: TextView = vista.findViewById(R.id.titulo_producto_basket)
        val description: TextView = vista.findViewById(R.id.descripcion_producto_basket)
        val price: TextView = vista.findViewById(R.id.precio_producto_basket)
        val deleteButton: ImageButton = vista.findViewById(R.id.delete_button_basket)


        title.text = currentGift.name
        description.text = currentGift.description
        price.text = String.format("%.2f", currentGift.price)

        image.setOnClickListener {
            val intent = Intent(context, GiftDetailActivity::class.java)
            intent.putExtra("GIFT_ID", currentGift.id)
            context!!.startActivity(intent)
        }

        deleteButton.setOnClickListener {
            if (context is BasketActivity) {
                (context as BasketActivity).removeItemFromBasket(currentGift)
            }
        }

        return vista
    }
}