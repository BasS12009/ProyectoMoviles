package mx.edu.itson.potros.wrapsy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BasketActivity : BaseActivity(){
    var adapter: ProductoAdapterBasket? = null
    var productos = ArrayList<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)

        val btnPayOrder: Button = findViewById(R.id.btn_pay_order)

        setupBottomNavigation()
        cargarProductos()

        btnPayOrder.setOnClickListener() {
            val intent = Intent(this, ChooseCardActivity::class.java)
            startActivity(intent)
        }

        adapter = ProductoAdapterBasket( this, productos)
        var gridProducto: GridView = findViewById(R.id.productos_catalogo)

        gridProducto.adapter = adapter
    }
    fun cargarProductos(){
        productos.add(Producto("Anastasia Beverly Hills", R.drawable.velvet_lipstick,"Lip velvet liquid lipstick",200.5))


    }
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
        var vista = inflator.inflate(R.layout.producto_basket, null)
        var image: ImageView = vista.findViewById(R.id.image_producto_basket)
        var title: TextView = vista.findViewById(R.id.titulo_producto_basket)
        var descripcion: TextView = vista.findViewById(R.id.descripcion_producto_basket)
        var precio: TextView = vista.findViewById(R.id.precio_producto_basket)


        image.setImageResource(producto.image)
        title.setText(producto.titulo)
        descripcion.setText(producto.descripcion)
        precio.text = String.format("%.2f", producto.precio)



        image.setOnClickListener() {
            val intento = Intent(context, ProductDetailActivity::class.java)
            intento.putExtra("titulo", producto.titulo)
            intento.putExtra("imagen", producto.image)
            intento.putExtra("descripcion", producto.descripcion)
            intento.putExtra("precio", producto.precio)
            context!!.startActivity(intento)
        }

        return vista
    }
}