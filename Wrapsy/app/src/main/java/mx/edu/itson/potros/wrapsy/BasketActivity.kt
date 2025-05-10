package mx.edu.itson.potros.wrapsy

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mx.edu.itson.potros.wrapsy.Adapters.GiftBasketAdapter
import mx.edu.itson.potros.wrapsy.Entities.Gift

class BasketActivity : BaseActivity() {

    private lateinit var btnPayOrder: Button
    private lateinit var basketListView: GridView
    private lateinit var giftBasketAdapter: GiftBasketAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private var basketItems: ArrayList<Gift> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BasketActivity", "onCreate() started") // <-- AGREGAR ESTE LOG
        setContentView(R.layout.activity_basket)

        setupBottomNavigation()

        btnPayOrder = findViewById(R.id.btn_pay_order)
        basketListView = findViewById(R.id.basket_list_view)
        sharedPreferences = getSharedPreferences("basket_prefs", Context.MODE_PRIVATE)

        Log.d("BasketActivity", "Views initialized") // <-- AGREGAR ESTE LOG

        loadBasketItems()

        Log.d("BasketActivity", "loadBasketItems() called") // <-- VERIFICAR SI SE LLEGA AQUÍ

        giftBasketAdapter = GiftBasketAdapter(this, basketItems)
        basketListView.adapter = giftBasketAdapter

        btnPayOrder.setOnClickListener {
            //
        }
    }

    private fun loadBasketItems() {
        Log.d("BasketActivity", "loadBasketItems() started") // <-- AGREGAR ESTE LOG
        val basketListJson = sharedPreferences.getString("basket_items", null)
        Log.d("BasketActivity", "Raw JSON from SharedPreferences: $basketListJson")
        basketItems.clear()
        if (!basketListJson.isNullOrEmpty()) {
            val type = object : TypeToken<ArrayList<Gift>>() {}.type
            val storedBasketItems: ArrayList<Gift>? = gson.fromJson(basketListJson, type)
            if (storedBasketItems != null) {
                basketItems.addAll(storedBasketItems)
                Log.d("BasketActivity", "Number of items loaded: ${basketItems.size}")
                basketItems.forEach { Log.d("BasketActivity", "Loaded item: ${it.name}") }
            } else {
                Log.d("BasketActivity", "Stored basket items were null after Gson.");
            }
        } else {
            Log.d("BasketActivity", "No basket items found in SharedPreferences.");
        }
    }


    fun removeItemFromBasket(giftToRemove: Gift) {
        basketItems.remove(giftToRemove)
        val updatedBasketListJson = gson.toJson(basketItems)
        sharedPreferences.edit().putString("basket_items", updatedBasketListJson).apply()
        giftBasketAdapter.notifyDataSetChanged() // Notificamos al adapter que los datos cambiaron
    }
}