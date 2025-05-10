package mx.edu.itson.potros.wrapsy.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.itson.potros.wrapsy.Entities.Purchase
import mx.edu.itson.potros.wrapsy.R

class PurchaseAdapter(private val purchases: List<Purchase>) :
    RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder>() {

    var onItemClick: ((Purchase) -> Unit)? = null

    inner class PurchaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(purchases[adapterPosition])
            }
        }
        val ivPurchase: ImageView = itemView.findViewById(R.id.iv_purchase)
        val tvOrderNumber: TextView = itemView.findViewById(R.id.tv_order)
        val tvDeliveryDate: TextView = itemView.findViewById(R.id.tv_delivery_date)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_purchase, parent, false)
        return PurchaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: PurchaseViewHolder, position: Int) {
        val purchase = purchases[position]

        // Obtener el primer giftId (si existe)
        val firstGiftId = purchase.giftIds.firstOrNull()

        // Cargar datos del regalo desde Firestore (ejemplo)
        if (firstGiftId != null) {
            FirebaseFirestore.getInstance().collection("Gift")
                .document(firstGiftId)
                .get()
                .addOnSuccessListener { giftSnapshot ->
                    val giftName = giftSnapshot.getString("name") ?: "Gift not available"
                    val imageUrl = giftSnapshot.getString("imageUrl")

                    holder.tvOrderNumber.text = purchase.orderNumber
                    Glide.with(holder.itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.heisenberg_plush)
                        .into(holder.ivPurchase)
                }
        }

        holder.tvDeliveryDate.text = "Arriving on ${purchase.estimatedDelivery}"
        holder.tvStatus.text = purchase.status
    }

    override fun getItemCount() = purchases.size
}