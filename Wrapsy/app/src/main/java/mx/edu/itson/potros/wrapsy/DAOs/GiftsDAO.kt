package mx.edu.itson.potros.wrapsy.DAOs

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.itson.potros.wrapsy.Entities.Gift
import mx.edu.itson.potros.wrapsy.R

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

    fun saveMockGifts() {
        val dao = GiftsDAO()

        val mockGifts = listOf(
            // Details
            Gift(
                name = "puma cap John Cena",
                price = 200.00,
                imageResourceId = R.drawable.john_cena_cap,
                categories = listOf("details"),
                isFavorite = false
            ),
            Gift(
                name = "Lipstick Anastasia",
                price = 500.00,
                imageResourceId = R.drawable.pinturas_anastasia,
                categories = listOf("details"),
                isFavorite = false
            ),
            Gift(
                name = "Quintet Eyeshadow",
                price = 100.00,
                imageResourceId = R.drawable.quintet_yeshadow,
                categories = listOf("details"),
                isFavorite = false
            ),

            // Balloons
            Gift(
                name = "Tulip Flower Balloon",
                price = 100.00,
                imageResourceId = R.drawable.tulip_flower_balloon,
                categories = listOf("balloons"),
                isFavorite = false
            ),
            Gift(
                name = "Roses Flower Balloon",
                price = 150.00,
                imageResourceId = R.drawable.roses_flower_balloon,
                categories = listOf("balloons"),
                isFavorite = false
            ),
            Gift(
                name = "Rabbit balloon",
                price = 130.00,
                imageResourceId = R.drawable.rabbit_balloon,
                categories = listOf("balloons"),
                isFavorite = false
            ),

            // Plush Toys
            Gift(
                name = "Heisenberg Plush",
                price = 150.00,
                imageResourceId = R.drawable.heisenberg_plush,
                categories = listOf("plush_toys"),
                isFavorite = false
            ),
            Gift(
                name = "Flower Plush",
                price = 110.00,
                imageResourceId = R.drawable.flores_tejidas,
                categories = listOf("plush_toys"),
                isFavorite = false
            ),
            Gift(
                name = "Strawberries Plush",
                price = 200.00,
                imageResourceId = R.drawable.fresitas_tqm,
                categories = listOf("plush_toys"),
                isFavorite = false
            ),

            // Mugs
            Gift(
                name = "Red Mug With Hearts",
                price = 500.00,
                imageResourceId = R.drawable.heart_mug,
                categories = listOf("mugs"),
                isFavorite = false
            ),
            Gift(
                name = "Hand Made Ceramic Mug",
                price = 700.00,
                imageResourceId = R.drawable.starwberry_mug,
                categories = listOf("mugs"),
                isFavorite = false
            ),
            Gift(
                name = "Red Star Mug",
                price = 300.00,
                imageResourceId = R.drawable.star_mug,
                categories = listOf("mugs"),
                isFavorite = false
            )
        )

        mockGifts.forEach { gift ->
            dao.saveGift(gift)
        }
    }

}