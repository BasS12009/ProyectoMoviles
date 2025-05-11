package mx.edu.itson.potros.wrapsy.DAOs

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.Entities.GiftList

class GiftListDAO {
    private val firestore = FirebaseFirestore.getInstance()
    private val giftListsCollection = firestore.collection("giftLists")
    private val TAG = "GiftListDAO"

    suspend fun createGiftList(giftList: GiftList): String {
        return try {
            val documentReference = giftListsCollection.add(giftList.toMap()).await()
            Log.d(TAG, "GiftList creada con ID: ${documentReference.id}")
            documentReference.id
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear GiftList", e)
            throw e
        }
    }

    suspend fun getAllGiftListsByUserId(userId: String): List<GiftList> {
        return try {
            val querySnapshot = giftListsCollection.whereEqualTo("userID", userId).get().await()
            val giftLists = querySnapshot.documents.mapNotNull { GiftList.fromDocumentSnapshot(it) }
            Log.d(TAG, "Se obtuvieron ${giftLists.size} GiftLists para el usuario con ID: $userId")
            giftLists
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener GiftLists para el usuario con ID: $userId", e)
            emptyList()
        }
    }

    suspend fun deleteGiftList(giftListId: String) {
        try {
            giftListsCollection.document(giftListId).delete().await()
            Log.d(TAG, "GiftList con ID: $giftListId eliminada")
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar GiftList con ID: $giftListId", e)
            throw e
        }
    }

    suspend fun addGiftToGiftList(giftListId: String, gift: Gift) {
        try {
            val documentReference = giftListsCollection.document(giftListId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(documentReference)
                val existingGiftList = GiftList.fromDocumentSnapshot(snapshot)
                if (existingGiftList != null) {
                    val updatedGifts = existingGiftList.gifts.toMutableList().apply { add(gift) }
                    transaction.update(documentReference, "gifts", updatedGifts.map { it.toMap() })
                    Log.d(TAG, "Se añadió el regalo '${gift.name}' a la GiftList con ID: $giftListId")
                    null
                } else {
                    val errorMessage = "GiftList con ID $giftListId no encontrada"
                    Log.w(TAG, errorMessage)
                    throw Exception(errorMessage)
                }
            }.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error al añadir regalo a la GiftList con ID: $giftListId", e)
            throw e
        }
    }
}
