package mx.edu.itson.potros.wrapsy.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.R

class PurchasedGiftAdapter(private val gifts: List<Gift>) :
    RecyclerView.Adapter<PurchasedGiftAdapter.GiftViewHolder>() {

    inner class GiftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivGift: ImageView = itemView.findViewById(R.id.iv_purchase)
        val tvGiftName: TextView = itemView.findViewById(R.id.tv_order)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_purchased_gift, parent, false)
        return GiftViewHolder(view)
    }

    override fun onBindViewHolder(holder: GiftViewHolder, position: Int) {
        val gift = gifts[position]
        holder.tvGiftName.text = gift.name
        holder.tvPrice.text = "$${gift.price}"

        // Cargar imagen con Glide
        Glide.with(holder.itemView.context)
            .load(gift.imageResourceId) // O usar imageUrl si lo tienes en Firebase
            .placeholder(R.drawable.heisenberg_plush)
            .into(holder.ivGift)
    }

    override fun getItemCount() = gifts.size
}