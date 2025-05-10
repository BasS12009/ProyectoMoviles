package mx.edu.itson.potros.wrapsy.Entities


data class Basket(
    val id: String = "",
    val userId: String = "",
    val gifts: MutableList<Gift> = mutableListOf(),
    val totalPrice: Double = 0.0
) {

    fun recalculateTotalPrice(): Basket {
        val sum = gifts.sumOf { it.price }
        return this.copy(totalPrice = sum)
    }

    fun addGift(gift: Gift): Basket {
        gifts.add(gift)
        return recalculateTotalPrice()
    }

    fun removeGift(gift: Gift): Basket {
        gifts.remove(gift)
        return recalculateTotalPrice()
    }
}
