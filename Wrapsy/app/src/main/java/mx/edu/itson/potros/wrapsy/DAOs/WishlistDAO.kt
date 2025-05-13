package mx.edu.itson.potros.wrapsy.DAOs

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.Entities.Wishlist

class WishlistDAO {
    private val db = FirebaseFirestore.getInstance()
    private val wishlistsCollection = db.collection("Wishlist")

    suspend fun getOrCreateWishlist(userId: String): Wishlist? { // El retorno ahora es nullable
        return try {
            getWishlistByUser(userId) ?: createUserWishlist(userId)
        } catch (e: Exception) {
            Log.e("WishlistDAO", "Error: ${e.message}")
            null
        }
    }

    suspend fun addGiftToWishlist(wishlistId: String, gift: Gift): Boolean {
        return try {
            wishlistsCollection.document(wishlistId)
                .update("gifts", FieldValue.arrayUnion(gift.toMap()))
                .await()
            true
        } catch (e: Exception) {
            Log.e("WishlistDAO", "Error añadiendo regalo: ${e.message}")
            false
        }
    }

    private suspend fun getWishlistByUser(userId: String): Wishlist? {
        val query = wishlistsCollection
            .whereEqualTo("userID", userId)
            .limit(1)
            .get()
            .await()

        return query.documents.firstOrNull()?.let { doc ->
            Wishlist(
                id = doc.id,
                userID = doc.getString("userID") ?: "",
                gifts = parseGifts(doc.get("gifts") as? List<Map<String, Any>>)
            )
        }
    }

    private suspend fun createUserWishlist(userId: String): Wishlist {
        val newWishlist = hashMapOf(
            "userId" to userId,
            "gifts" to emptyList<String>(),
            "createdAt" to FieldValue.serverTimestamp()
        )

        val docRef = wishlistsCollection.add(newWishlist).await()
        return Wishlist(
            id = docRef.id,
            userID = userId,
            gifts = emptyList()
        )
    }

    private fun parseGifts(giftsData: List<Map<String, Any>>?): List<Gift> {
        return giftsData?.mapNotNull { Gift.fromMap(it) } ?: emptyList()
    }

}