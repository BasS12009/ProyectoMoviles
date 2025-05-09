package mx.edu.itson.potros.wrapsy.DAOs

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.itson.potros.wrapsy.Entities.Comment
import mx.edu.itson.potros.wrapsy.Entities.Gift

class CommentsDAO {

    fun saveComment(giftId: String, comment: Comment) {
        val db = FirebaseFirestore.getInstance()
        val commentRef = db.collection("Gifts").document(giftId).collection("Comments").document()

        comment.id = commentRef.id // Asignar ID generado por Firestore
        commentRef.set(comment)
            .addOnSuccessListener {
                updateAverageRating(giftId) // Actualizar rating promedio
                Log.d("Firestore", "Comentario guardado: ${comment.id}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error guardando comentario", e)
            }
    }



    fun getCommentsForGift(giftId: String, onSuccess: (List<Comment>) -> Unit) {
        FirebaseFirestore.getInstance().collection("Gifts").document(giftId)
            .collection("Comments")
            .get()
            .addOnSuccessListener { result ->
                val comments = result.map {
                    Comment(
                        id = it.id,
                        title = it.getString("title") ?: "",
                        text = it.getString("text") ?: "",
                        rating = it.getDouble("rating")?.toInt() ?: 0
                    )
                }
                onSuccess(comments)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error obteniendo comentarios", e)
                onSuccess(emptyList())
            }
    }

    fun saveMockComments() {
        val giftsDao = GiftsDAO()
        giftsDao.getAllGifts { gifts ->
            gifts.forEach { gift ->
                createMockComments(gift).forEach { comment ->
                    saveComment(gift.id, comment)
                }
            }
        }
    }

    private fun createMockComments(gift: Gift): List<Comment> {
        return when (gift.name) {
            "puma cap John Cena" -> listOf(
                Comment(
                    title = "Unique style!",
                    text = "John Cena's cap is perfect for fans. It looks even better than in the pictures 🎩",
                    rating = 5
                ),
                Comment(
                    title = "Good price",
                    text = "For $200 it's good quality, although the fabric could be thicker",
                    rating = 4
                )
            )

            "Lipstick Anastasia" -> listOf(
                Comment(
                    title = "Spectacular color",
                    text = "The shade lasts all day and the packaging is luxurious 💄",
                    rating = 5
                ),
                Comment(
                    title = "Expensive for what it is",
                    text = "Good product but $500 is too much for a lipstick",
                    rating = 3
                )
            )

            "Under Armour Sneakers" -> listOf(
                Comment(
                    title = "Incredible comfort",
                    text = "Worth every penny! The best sneakers for training 🏃‍♂️",
                    rating = 5
                ),
                Comment(
                    title = "Size issue",
                    text = "I received a size larger than specified",
                    rating = 2
                )
            )

            "Tulip Flower Balloon" -> listOf(
                Comment(
                    title = "Perfect decoration",
                    text = "The tulip balloon stayed inflated for 3 days! 🌷",
                    rating = 4
                )
            )

            "Roses Flower Balloon" -> listOf(
                Comment(
                    title = "Beautiful detail",
                    text = "The latex roses look realistic 💐",
                    rating = 5
                ),
                Comment(
                    title = "Fragility",
                    text = "It deflated faster than expected",
                    rating = 3
                )
            )

            "Rabbit balloon" -> listOf(
                Comment(
                    title = "Adorable rabbit",
                    text = "Perfect for a baby shower! 🐇",
                    rating = 5
                )
            )

            "Heisenberg Plush" -> listOf(
                Comment(
                    title = "For Breaking Bad fans",
                    text = "The plush quality exceeded expectations! 🧪",
                    rating = 5
                ),
                Comment(
                    title = "Small size",
                    text = "It's smaller than I thought",
                    rating = 4
                )
            )

            "Flower Plush" -> listOf(
                Comment(
                    title = "Textile art",
                    text = "The knitted flowers are a beautiful handmade piece 🌸",
                    rating = 5
                )
            )

            "Strawberries Plush" -> listOf(
                Comment(
                    title = "Incredible softness",
                    text = "The little strawberries are so soft I want 10 more! 🍓",
                    rating = 5
                ),
                Comment(
                    title = "High price",
                    text = "Cute but $200 is too much for its size",
                    rating = 3
                )
            )

            "Red Mug With Hearts" -> listOf(
                Comment(
                    title = "Romantic mug",
                    text = "Perfect for gifting on February 14th ❤️",
                    rating = 5
                )
            )

            "Hand Made Ceramic Mug" -> listOf(
                Comment(
                    title = "Unique craftsmanship",
                    text = "You can tell it's handmade, every detail counts 🫖",
                    rating = 5
                ),
                Comment(
                    title = "Fragility",
                    text = "Beautiful but it arrived with a small crack",
                    rating = 2
                )
            )

            "Red Star Mug" -> listOf(
                Comment(
                    title = "Spectacular shine",
                    text = "The red star shines with the hot coffee 🌟",
                    rating = 4
                )
            )

            else -> listOf(
                Comment(
                    title = "Good product",
                    text = "Meets expectations",
                    rating = 4
                )
            )
        }
    }

    private fun updateAverageRating(giftId: String) {
        getCommentsForGift(giftId) { comments ->
            if (comments.isEmpty()) return@getCommentsForGift

            val average = comments.map { it.rating }.average()
            FirebaseFirestore.getInstance().collection("Gifts")
                .document(giftId)
                .update("averageRating", average)
                .addOnSuccessListener {
                    Log.d("Firestore", "Rating actualizado: $average")
                }
        }
    }
}