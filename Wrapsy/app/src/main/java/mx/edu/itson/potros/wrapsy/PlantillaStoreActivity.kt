package mx.edu.itson.potros.wrapsy

import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity

class PlantillaStoreActivity : AppCompatActivity() {
    var adapter: ProductoAdapter? = null
    var productos = ArrayList<Producto>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommended_gifts)

        cargarProductos()

        adapter = ProductoAdapter( this, productos)
        var gridProducto: GridView = findViewById(R.id.productos_catalogo)

        gridProducto.adapter = adapter
    }
    fun cargarProductos(){
        productos.add(Producto("Anastasia Beverly Hills", R.drawable.velvet_lipstick,"Lip velvet liquid lipstick"))
        productos.add(Producto("Anastasia Beverly Hills", R.drawable.dipbrow_pomade,"Dipbrow Pomade Waterproof"))
        productos.add(Producto("CHANEL", R.drawable.rouge_allure_velvet,"Rouge Allure Velvet Les Perles"))
        productos.add(Producto("CHANEL", R.drawable.joues_contraste_intense,"Joues Contraste Intense"))
        productos.add(Producto("Fenty Beauty", R.drawable.arcane_mel_gold,"Arcane Mel Gold Diamond Bomb"))
        productos.add(Producto("Fenty Beauty", R.drawable.arcane_gloss_bomb,"Arcane gloss bomb"))


    }
}
