package mx.edu.itson.potros.wrapsy

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.bumptech.glide.Glide
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.data.BasketDAO

class GiftBasketAdapter(
    var context: Context?,
    private val giftIds: MutableList<String>,
    private val basketDAO: BasketDAO,
    private val userId: String
) : BaseAdapter() {

    private var gifts: MutableList<Gift> = mutableListOf()

    init {
        Log.d("GiftBasketAdapter", "Adapter initialized with ${giftIds.size} item IDs for user $userId.")
        loadGiftDetails()
    }

    private fun loadGiftDetails() {
        if (context is BasketActivity) {
            (context as BasketActivity).lifecycleScope.launch {
                gifts.clear()
                giftIds.forEach { giftId ->
                    val gift = basketDAO.getGiftById(giftId)
                    gift?.let { gifts.add(it) }
                }
                notifyDataSetChanged()
            }
        }
    }

    override fun getCount(): Int {
        return gifts.size
    }

    override fun getItem(position: Int): Gift {
        return gifts[position]
    }

    override fun getItemId(position: Int): Long {
        return gifts[position].id.hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_basket, null)
        }

        val currentGift = getItem(position)
        val image: ImageView = view!!.findViewById(R.id.image_producto_basket)
        val title: TextView = view.findViewById(R.id.titulo_producto_basket)
        val description: TextView = view.findViewById(R.id.descripcion_producto_basket)
        val price: TextView = view.findViewById(R.id.precio_producto_basket)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_button_basket)

        title.text = currentGift.name
        description.text = currentGift.description
        price.text = String.format("%.2f", currentGift.price)

        Glide.with(context!!)
            .load(currentGift.imageResourceId) // Asegúrate que esta propiedad exista y sea correcta
            .placeholder(R.drawable.ic_wrapsy) // Usa placeholder mientras carga
            .error(R.drawable.ic_wrapsy)     // Usa imagen de error si falla la carga
            .into(image)

        image.setOnClickListener {
            val intent = Intent(context, GiftDetailActivity::class.java)
            intent.putExtra("GIFT_ID", currentGift.id)
            context!!.startActivity(intent)
        }

        deleteButton.setOnClickListener {
            if (context is BasketActivity) {
                (context as BasketActivity).lifecycleScope.launch {
                    val giftIdToRemove = currentGift.id
                    val isRemoved = basketDAO.removeGiftIdFromBasket(userId, giftIdToRemove)
                    if (isRemoved) {
                        giftIds.remove(giftIdToRemove)
                        loadGiftDetails()
                        (context as BasketActivity).updateTotalPrice(giftIds) // Pasar la lista de IDs para recalcular el precio
                    } else {
                        Log.e("GiftBasketAdapter", "Failed to remove gift with ID $giftIdToRemove from basket for user $userId")
                        // Opcional: Mostrar un mensaje al usuario sobre el fallo
                    }
                }
            }
        }

        return view
    }
}