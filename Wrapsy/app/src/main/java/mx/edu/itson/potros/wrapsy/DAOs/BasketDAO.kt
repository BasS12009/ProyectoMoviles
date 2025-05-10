package mx.edu.itson.potros.wrapsy.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import mx.edu.itson.potros.wrapsy.Entities.Basket
import mx.edu.itson.potros.wrapsy.Entities.Gift


class BasketDAO {

    private val firestore = FirebaseFirestore.getInstance()
    private val basketsCollection = firestore.collection("baskets")
    private val giftsCollection = firestore.collection("gifts")
    private val TAG = "BasketDAO"

    suspend fun addGiftToBasket(userId: String, gift: Gift): Boolean {
        return try {
            val querySnapshot = basketsCollection.whereEqualTo("userId", userId).limit(1).get().await()

            if (!querySnapshot.isEmpty) {
                val basketDocument = querySnapshot.documents[0]
                val existingBasket = Basket.fromDocumentSnapshot(basketDocument)
                existingBasket.gifts.add(gift)
                val updatedBasketMap = existingBasket.toMap()
                basketsCollection.document(basketDocument.id).set(updatedBasketMap).await()
                true
            } else {
                val newBasket = Basket(userId = userId).apply { gifts.add(gift) }
                basketsCollection.add(newBasket.toMap()).await()
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding gift to basket for user $userId", e)
            false
        }
    }

    suspend fun removeGiftFromBasket(userId: String, giftIdToRemove: String): Boolean {
        return try {
            val querySnapshot = basketsCollection.whereEqualTo("userId", userId).limit(1).get().await()

            if (!querySnapshot.isEmpty) {
                val basketDocument = querySnapshot.documents[0]
                val existingBasket = Basket.fromDocumentSnapshot(basketDocument)
                val initialSize = existingBasket.gifts.size
                existingBasket.gifts.removeAll { it.id == giftIdToRemove } // Asumiendo que Gift tiene un 'id'
                if (existingBasket.gifts.size < initialSize) {
                    val updatedBasketMap = existingBasket.toMap()
                    basketsCollection.document(basketDocument.id).set(updatedBasketMap).await()
                    true
                } else {
                    Log.w(TAG, "Gift with ID $giftIdToRemove not found in basket for user $userId")
                    false
                }
            } else {
                Log.w(TAG, "Basket not found for user $userId when trying to remove gift $giftIdToRemove")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing gift $giftIdToRemove from basket for user $userId", e)
            false
        }
    }

    suspend fun getAllGiftsInBasketForUser(userId: String): List<Gift> {
        return try {
            val querySnapshot = basketsCollection.whereEqualTo("userId", userId).limit(1).get().await()

            if (!querySnapshot.isEmpty) {
                val basketDocument = querySnapshot.documents[0]
                val existingBasket = Basket.fromDocumentSnapshot(basketDocument)
                existingBasket.gifts
            } else {
                Log.i(TAG, "No basket found for user $userId, returning empty list of gifts.")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all gifts in basket for user $userId", e)
            emptyList()
        }
    }
}
