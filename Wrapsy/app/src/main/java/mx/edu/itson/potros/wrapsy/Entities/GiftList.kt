package mx.edu.itson.potros.wrapsy.Entities

import com.google.firebase.firestore.DocumentSnapshot
import kotlin.collections.List

data class GiftList(
    val id: String = "",
    val name: String,
    val gifts: List<Gift>,
    var userID: String
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "gifts" to gifts.map { it.toMap() },
            "userID" to userID
        )
    }

    companion object {
        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): GiftList? {
            val id = snapshot.id
            val name = snapshot.getString("name") ?: return null
            val giftsSnapshotList = snapshot.get("gifts") as? List<Map<String, Any>> ?: listOf()
            val gifts = giftsSnapshotList.mapNotNull { Gift.fromMap(it) }
            val userID = snapshot.getString("userID") ?: return null

            return GiftList(id, name, gifts, userID)
        }
    }
}