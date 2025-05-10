package mx.edu.itson.potros.wrapsy.Entities


import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson

data class Basket(
    var id: String = "", // Permitimos la modificación del ID después de la creación
    var userId: String = "",
    val gifts: MutableList<Gift> = mutableListOf(),
    var totalPrice: Double = 0.0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            // Necesitas serializar la lista de Gifts a un formato que Firestore pueda almacenar bien
            "gifts" to gifts.map { Gson().toJson(it) }, // Guardamos cada Gift como un JSON String
            "totalPrice" to totalPrice,
        )
    }

    companion object {
        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): Basket {
            val basket = Basket()
            basket.id = snapshot.id
            basket.userId = snapshot.getString("userId") ?: ""

            val giftsStringList = snapshot.get("gifts") as? List<String> ?: listOf()
            basket.gifts.addAll(giftsStringList.mapNotNull {
                try {
                    Gson().fromJson(it, Gift::class.java)
                } catch (e: Exception) {
                    Log.e(TAG, "Error deserializando Gift desde Firestore: $it", e)
                    null
                }
            }.toMutableList())
            basket.totalPrice = snapshot.getDouble("totalPrice") ?: snapshot.getLong("totalPrice")?.toDouble() ?: 0.0

            return basket
        }
    }
}