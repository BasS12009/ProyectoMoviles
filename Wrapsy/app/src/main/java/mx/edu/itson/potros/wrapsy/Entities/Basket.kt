package mx.edu.itson.potros.wrapsy.Entities


import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson

data class Basket(
    var id: String = "",
    var userId: String = "",
    val giftIds: MutableList<String> = mutableListOf(),
    var totalPrice: Double = 0.0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "giftIds" to giftIds,
            "totalPrice" to totalPrice,
        )
    }

    companion object {
        private const val TAG = "BasketEntity"

        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): Basket {
            val basket = Basket()
            basket.id = snapshot.id
            basket.userId = snapshot.getString("userId") ?: ""

            val giftIdsFromFirestore = snapshot.get("giftIds") as? List<String> ?: listOf()
            basket.giftIds.addAll(giftIdsFromFirestore)

            basket.totalPrice = snapshot.getDouble("totalPrice") ?: snapshot.getLong("totalPrice")?.toDouble() ?: 0.0

            return basket
        }
    }
}