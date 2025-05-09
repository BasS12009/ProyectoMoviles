package mx.edu.itson.potros.wrapsy.DAOs

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.itson.potros.wrapsy.Entities.Gift

class GiftsDAO {

    fun saveGift(gift: Gift) {
        val db = FirebaseFirestore.getInstance()

        val documentRef = if (gift.id.isEmpty()) {
            // Crear nuevo documento con ID automático
            db.collection("Gifts").document()
        } else {
            // Usar documento existente
            db.collection("Gifts").document(gift.id)
        }

        if (gift.id.isEmpty()) {
            gift.id = documentRef.id
        }

        documentRef.set(gift.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "Documento guardado con ID: ${documentRef.id}")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error guardando", it)
            }
    }

    fun getGift(giftId: String, onSuccess: (Gift?) -> Unit) {
        FirebaseFirestore.getInstance().collection("Gifts")
            .document(giftId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    onSuccess(Gift.fromDocumentSnapshot(snapshot))
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error obteniendo", it)
                onSuccess(null)
            }
    }

    fun getGiftsByCategory(
        category: String,
        onSuccess: (List<Gift>) -> Unit
    ) {
        FirebaseFirestore.getInstance().collection("Gifts")
            .whereArrayContains("categories", category) // Filtra por categoría
            .get()
            .addOnSuccessListener { querySnapshot ->
                val gifts = mutableListOf<Gift>()
                for (document in querySnapshot.documents) {
                    // Convertir cada documento a Gift
                    gifts.add(Gift.fromDocumentSnapshot(document))
                }
                onSuccess(gifts)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al obtener por categoría", exception)
                onSuccess(emptyList()) // Retorna lista vacía en caso de error
            }
    }

}