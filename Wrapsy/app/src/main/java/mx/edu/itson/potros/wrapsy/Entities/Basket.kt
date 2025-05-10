package mx.edu.itson.potros.wrapsy.Entities


data class Basket(
    val id: String = "",
    val userId: String = "",
    val gifts: MutableList<Gift> = mutableListOf(),
    val totalPrice: Double = 0.0
) {


}
