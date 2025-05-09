package mx.edu.itson.potros.wrapsy.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.GiftDetailActivity
import mx.edu.itson.potros.wrapsy.R

class GiftBasketAdapter (var context: Context?, var gift: ArrayList<Gift>) : BaseAdapter() {
    override fun getCount(): Int {
        return gift.size
    }

    override fun getItem(p0: Int): Any {
        return gift[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(pe: Int, p1: View?, p2: ViewGroup?): View {
        var gift = gift[pe]
        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var vista = inflator.inflate(R.layout.item_basket, null)
        var image: ImageView = vista.findViewById(R.id.image_producto_basket)
        var title: TextView = vista.findViewById(R.id.titulo_producto_basket)
        var descripcion: TextView = vista.findViewById(R.id.descripcion_producto_basket)
        var precio: TextView = vista.findViewById(R.id.precio_producto_basket)


        image.setImageResource(gift.imageResourceId)
        title.setText(gift.name)
        descripcion.setText(gift.description)
        precio.text = String.format("%.2f", gift.price)



        image.setOnClickListener() {
            val intento = Intent(context, GiftDetailActivity::class.java)
            intento.putExtra("titulo", gift.name)
            intento.putExtra("imagen", gift.imageResourceId)
            intento.putExtra("descripcion", gift.description)
            intento.putExtra("precio", gift.price)
            context!!.startActivity(intento)
        }

        return vista
    }
}
