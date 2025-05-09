package mx.edu.itson.potros.wrapsy.DAOs

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import mx.edu.itson.potros.wrapsy.Entities.Purchase

class PurchaseDAO {
    fun savePurchase(purchase: Purchase, onSuccess: (String?) -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        val documentRef = if (purchase.id.isNullOrEmpty()) {
            db.collection("Purchases").document()
        } else {
            db.collection("Purchases").document(purchase.id!!)
        }

        if (purchase.id.isNullOrEmpty()) {
            purchase.id = documentRef.id
        }

        documentRef.set(purchase.toMap())
            .addOnSuccessListener {
                Log.d("Firestore", "Compra guardada con ID: ${documentRef.id}")
                onSuccess(documentRef.id)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error guardando compra", e)
                onFailure(e)
            }
    }


        fun getPurchasesByUser(userId: String, onSuccess: (List<Purchase>) -> Unit, onFailure: (Exception) -> Unit) {
            FirebaseFirestore.getInstance().collection("Purchases")
                .whereEqualTo("userId", userId)
                .orderBy("orderDate", Query.Direction.DESCENDING) // Opcional: ordenar por fecha descendente
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val purchases = querySnapshot.documents.map {
                        Purchase.fromDocumentSnapshot(it)
                    }
                    onSuccess(purchases)
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error obteniendo compras por usuario", e)
                    onFailure(e)
                }
        }



}



