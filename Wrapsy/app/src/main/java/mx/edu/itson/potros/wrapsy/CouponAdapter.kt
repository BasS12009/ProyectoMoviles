package mx.edu.itson.potros.wrapsy


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CouponAdapter(
    private val context: Context,
    private val coupons: List<Coupon>,
    private val onCouponClickListener: (Coupon) -> Unit
) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

    // Colors
    private val pistacheColor = ContextCompat.getColor(context, R.color.pistache)
    private val cremaPalidoColor = ContextCompat.getColor(context, R.color.cremaPalido)
    private val whiteColor = ContextCompat.getColor(context, android.R.color.white)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_coupon, parent, false)
        return CouponViewHolder(view)
    }

    override fun getItemCount() = coupons.size

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val coupon = coupons[position]

        // Background color based on the "diagonal" pattern
        val row = position / 2
        val col = position % 2

        // The pattern repeats every 2 columns and shifts every 2 rows
        val isPistacheBackground = when ((row % 2) * 2 + col) {
            0 -> true
            1 -> false
            2 -> false
            3 -> true
            else -> true
        }

        // Set card background color
        val backgroundColor = if (isPistacheBackground) pistacheColor else cremaPalidoColor
        holder.cardView.setCardBackgroundColor(backgroundColor)

        // Set text color based on background
        val textColor = if (isPistacheBackground) whiteColor else pistacheColor
        holder.titleView.setTextColor(textColor)
        holder.descriptionView.setTextColor(textColor)
        holder.expiryDateView.setTextColor(textColor)

        // Set coupon data
        holder.titleView.text = coupon.title
        holder.descriptionView.text = coupon.description

        // Show expiry date if available
        if (coupon.expiryDate.isNotEmpty()) {
            holder.expiryDateView.visibility = View.VISIBLE
            holder.expiryDateView.text = coupon.expiryDate
        } else {
            holder.expiryDateView.visibility = View.GONE
        }

        // Set image based on coupon type and background color
        val imageResId = when (coupon.type) {
            "delivery" -> R.drawable.didi_envios_apurado
            "gift" -> R.drawable.ic_plushtoy
            "purchase" -> if (isPistacheBackground) R.drawable.carrito_blanco else R.drawable.carrito_verde
            else -> R.drawable.carrito_blanco // Default image
        }
        holder.imageView.setImageResource(imageResId)

        // Set click listener
        holder.cardView.setOnClickListener {
            onCouponClickListener(coupon)
        }
    }

    class CouponViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.couponCardView)
        val titleView: TextView = itemView.findViewById(R.id.couponTitle)
        val descriptionView: TextView = itemView.findViewById(R.id.couponDescription)
        val imageView: ImageView = itemView.findViewById(R.id.couponImage)
        val expiryDateView: TextView = itemView.findViewById(R.id.couponExpiryDate)
    }
}