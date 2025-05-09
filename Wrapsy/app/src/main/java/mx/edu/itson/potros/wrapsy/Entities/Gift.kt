package mx.edu.itson.potros.wrapsy.Entities

import android.media.Rating
import com.google.firebase.firestore.DocumentSnapshot

data class Gift(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var imageUrl: String = "",
    var categories: List<String> = listOf(),
    var isFavorite: Boolean = false,
    var imageResourceId: Int = 0,
    var rating: Double = 0.0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "description" to description,
            "price" to price,
            "imageUrl" to imageUrl,
            "categories" to categories,
            "isFavorite" to isFavorite,
            "imageResourceId" to imageResourceId
        )
    }

    companion object {
        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): Gift {
            return Gift().apply {
                id = snapshot.id
                name = snapshot.getString("name") ?: ""
                description = snapshot.getString("description") ?: ""
                price = snapshot.getDouble("price") ?: 0.0
                imageUrl = snapshot.getString("imageUrl") ?: ""
                categories = snapshot.get("categories") as? List<String> ?: listOf()
                isFavorite = snapshot.getBoolean("isFavorite") ?: false
                imageResourceId = (snapshot.getLong("imageResourceId")?.toInt() ?: 0)
            }
        }
    }
}
