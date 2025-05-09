package mx.edu.itson.potros.wrapsy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView

class PlantillaStoreActivity : BaseActivity() {
    var adapter: ProductoAdapterBasket? = null
    var productos = ArrayList<Producto>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantilla_store)

        cargarProductos()
        setupBottomNavigation()



        adapter = ProductoAdapterBasket(this, productos)
        var gridProducto: GridView = findViewById(R.id.productos_catalogo)

        gridProducto.adapter = adapter
    }

    fun cargarProductos() {
        productos.add(
            Producto(
                "Anastasia Beverly Hills",
                R.drawable.velvet_lipstick,
                "Lip velvet liquid lipstick",
                300.5
            )
        )
        productos.add(
            Producto(
                "Anastasia Beverly Hills",
                R.drawable.dipbrow_pomade,
                "Dipbrow Pomade Waterproof",
                500.6
            )
        )
        productos.add(
            Producto(
                "CHANEL",
                R.drawable.rouge_allure_velvet,
                "Rouge Allure Velvet Les Perles",
                100.6
            )
        )
        productos.add(
            Producto(
                "CHANEL",
                R.drawable.joues_contraste_intense,
                "Joues Contraste Intense",
                100.0
            )
        )
        productos.add(
            Producto(
                "Fenty Beauty",
                R.drawable.arcane_mel_gold,
                "Arcane Mel Gold Diamond Bomb",
                1000.4
            )
        )
        productos.add(
            Producto(
                "Fenty Beauty",
                R.drawable.arcane_gloss_bomb,
                "Arcane gloss bomb",
                100.7
            )
        )


    }

    class ProductoAdapterBasket(var context: Context?, var producto: ArrayList<Producto>) : BaseAdapter() {
        override fun getCount(): Int {
            return producto.size
        }

        override fun getItem(p0: Int): Any {
            return producto[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(pe: Int, p1: View?, p2: ViewGroup?): View {
            var producto = producto[pe]
            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var vista = inflator.inflate(R.layout.item_gift, null)
            var image: ImageView = vista.findViewById(R.id.image_producto_cell)
            var title: TextView = vista.findViewById(R.id.titulo_producto_cell)
            var descripcion: TextView = vista.findViewById(R.id.descripcion_producto_cell)


            image.setImageResource(producto.image)
            title.setText(producto.titulo)
            descripcion.setText(producto.descripcion)



            image.setOnClickListener() {
                val intento = Intent(context, ProductDetailActivity::class.java)
                intento.putExtra("titulo", producto.titulo)
                intento.putExtra("imagen", producto.image)
                intento.putExtra("descripcion", producto.descripcion)
                context!!.startActivity(intento)
            }

            return vista
        }
        }
}
