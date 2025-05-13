package mx.edu.itson.potros.wrapsy.Entities

import com.google.firebase.firestore.DocumentSnapshot

data class Wishlist(
    val id: String = "",
    val gifts: List<Gift>,
    var userID: String
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "gifts" to gifts.map { it.toMap() },
            "userID" to userID,
            "type" to "wishlist"
        )
    }

    companion object {
        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): Wishlist? {
            val id = snapshot.id
            val giftsSnapshotList = snapshot.get("gifts") as? List<Map<String, Any>> ?: listOf()
            val gifts = giftsSnapshotList.mapNotNull { Gift.fromMap(it) }
            val userID = snapshot.getString("userID") ?: return null
            val type = snapshot.getString("type")

            if (type == "wishlist") {
                return Wishlist(id, gifts, userID)
            }
            return null
        }
    }
}