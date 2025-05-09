package mx.edu.itson.potros.wrapsy.Entities

import java.util.Date

data class Comment(
    var id: String = "",
    val giftId: String = "",
    val title: String = "",
    val userName: String = "",
    val text: String = "",
    val rating: Int = 0,
    val timestamp: Date = Date()
)