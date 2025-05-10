package mx.edu.itson.potros.wrapsy.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import mx.edu.itson.potros.wrapsy.Entities.Basket
import mx.edu.itson.potros.wrapsy.Entities.Gift


class BasketDAO {

    private val firestore = FirebaseFirestore.getInstance()
    private val basketsCollection = firestore.collection("Basket")
    private val giftsCollection = firestore.collection("Gifts") // Asumiendo una colección separada para Gifts
    private val TAG = "BasketDao"

    suspend fun addGiftToBasket(userId: String, giftId: String): Boolean {
        return try {
            val querySnapshot = basketsCollection.whereEqualTo("userId", userId).limit(1).get().await()

            if (!querySnapshot.isEmpty) {
                val basketDocument = querySnapshot.documents[0]
                val existingBasket = Basket.fromDocumentSnapshot(basketDocument)
                existingBasket.giftIds.add(giftId)
                val updatedBasketMap = existingBasket.toMap()
                basketsCollection.document(basketDocument.id).set(updatedBasketMap).await()
                true
            } else {
                val newBasket = Basket(userId = userId, giftIds = mutableListOf(giftId))
                basketsCollection.add(newBasket.toMap()).await()
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding gift ID $giftId to basket for user $userId", e)
            false
        }
    }

    // Función para eliminar la ID de un Gift del carrito de un usuario
    suspend fun removeGiftFromBasket(userId: String, giftIdToRemove: String): Boolean {
        return try {
            val querySnapshot = basketsCollection.whereEqualTo("userId", userId).limit(1).get().await()

            if (!querySnapshot.isEmpty) {
                val basketDocument = querySnapshot.documents[0]
                val existingBasket = Basket.fromDocumentSnapshot(basketDocument)
                val initialSize = existingBasket.giftIds.size
                existingBasket.giftIds.removeAll { it == giftIdToRemove }
                if (existingBasket.giftIds.size < initialSize) {
                    val updatedBasketMap = existingBasket.toMap()
                    basketsCollection.document(basketDocument.id).set(updatedBasketMap).await()
                    true
                } else {
                    Log.w(TAG, "Gift ID $giftIdToRemove not found in basket for user $userId")
                    false
                }
            } else {
                Log.w(TAG, "Basket not found for user $userId when trying to remove gift ID $giftIdToRemove")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing gift ID $giftIdToRemove from basket for user $userId", e)
            false
        }
    }

    // Función para obtener todos los Gifts del carrito de un usuario (requiere consulta adicional)
    suspend fun getAllGiftsInBasketForUser(userId: String): List<Gift> {
        return try {
            val querySnapshot = basketsCollection.whereEqualTo("userId", userId).limit(1).get().await()

            if (!querySnapshot.isEmpty) {
                val basketDocument = querySnapshot.documents[0]
                val existingBasket = Basket.fromDocumentSnapshot(basketDocument)
                val giftIdsInBasket = existingBasket.giftIds

                // Consultar la colección de Gifts para obtener los detalles basados en los IDs
                val gifts: MutableList<Gift> = mutableListOf()
                if (giftIdsInBasket.isNotEmpty()) { // Only query if there are giftIds
                    val giftsQuerySnapshot = giftsCollection.whereIn("id", giftIdsInBasket).get().await()
                    for (doc in giftsQuerySnapshot.documents) {
                        val gift = Gift.fromDocumentSnapshot(doc) // Asegúrate de tener esta función en Gift
                        gift?.let { gifts.add(it) }
                    }
                }
                gifts
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