package mx.edu.itson.potros.wrapsy.Entities

import android.os.Build
import androidx.annotation.RequiresApi
import mx.edu.itson.potros.wrapsy.User
import java.time.LocalDate
import com.google.firebase.firestore.DocumentSnapshot
import java.time.format.DateTimeFormatter

data class Purchase(
    var id: String? = null,
    var status: String = "",
    var orderNumber: String = "",
    var quantity: Int = 0,
    var orderDate: String = "",
    var estimatedDelivery: String? = null,
    var totalCost: Double = 0.0,
    var giftIds: List<String> = listOf(),
    var userId: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf<String, Any?>(
            "status" to status,
            "orderNumber" to orderNumber,
            "quantity" to quantity,
            "orderDate" to orderDate,
            "estimatedDelivery" to estimatedDelivery,
            "totalCost" to totalCost,
            "giftIds" to giftIds,
            "userId" to userId
        )
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): Purchase {
            return Purchase().apply {
                id = snapshot.id
                status = snapshot.getString("status") ?: ""
                orderNumber = snapshot.getString("orderNumber") ?: ""
                quantity = (snapshot.getLong("quantity")?.toInt() ?: 0)
                orderDate = snapshot.getString("orderDate") ?: ""
                estimatedDelivery = snapshot.getString("estimatedDelivery")
                totalCost = snapshot.getDouble("totalCost") ?: 0.0
                giftIds = snapshot.get("giftIds") as? List<String> ?: listOf()
                userId = snapshot.getString("userId") ?: ""
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun formatDate(date: LocalDate): String {
            return date.format(dateFormatter)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun parseDate(dateString: String?): LocalDate? {
            return try {
                if (!dateString.isNullOrEmpty()) {
                    LocalDate.parse(dateString, dateFormatter)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
