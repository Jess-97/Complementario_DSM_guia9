package udb.edu.sv.dsm.sqliteapp


import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import udb.edu.sv.dsm.sqliteapp.model.Categoria
import udb.edu.sv.dsm.sqliteapp.model.Productos

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var managerCategoria: Categoria
    private lateinit var managerProductos: Productos

    private lateinit var txtIdDB: TextView      // ID del producto cargado con Buscar
    private lateinit var txtId: EditText        // Código a buscar
    private lateinit var txtNombre: EditText
    private lateinit var txtPrecio: EditText
    private lateinit var txtCantidad: EditText
    private lateinit var cmbCategorias: Spinner

    private val listaCategorias = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtIdDB = findViewById(R.id.txtIdDB)
        txtId = findViewById(R.id.txtId)
        txtNombre = findViewById(R.id.txtNombre)
        txtPrecio = findViewById(R.id.txtPrecio)
        txtCantidad = findViewById(R.id.txtCantidad)
        cmbCategorias = findViewById(R.id.cmbCategorias)

        managerCategoria = Categoria(this)
        managerProductos = Productos(this)
        setSpinnerCategorias()

        findViewById<Button>(R.id.btnAgregar).setOnClickListener(this)
        findViewById<Button>(R.id.btnActualizar).setOnClickListener(this)
        findViewById<Button>(R.id.btnEliminar).setOnClickListener(this)
        findViewById<Button>(R.id.btnBuscar).setOnClickListener(this)
    }

    private fun setSpinnerCategorias() {
        managerCategoria.insertValuesDefault()
        val c = managerCategoria.showAllCategoria()
        listaCategorias.clear()
        if (c != null) {
            while (c.moveToNext()) {          // sin duplicar la primera categoría
                listaCategorias.add(c.getString(1))
            }
            c.close()
        }
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaCategorias)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cmbCategorias.adapter = adaptador
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnAgregar -> agregar()
            R.id.btnActualizar -> actualizar()
            R.id.btnEliminar -> eliminar()
            R.id.btnBuscar -> buscar()
        }
    }

    // ---------- AGREGAR ----------
    private fun agregar() {
        if (!validarProducto()) return
        managerProductos.addNewProducto(
            idCategoriaSeleccionada(),
            txtNombre.text.toString().trim(),
            txtPrecio.text.toString().trim().toDouble(),
            txtCantidad.text.toString().trim().toInt()
        )
        mostrar("Producto agregado")
        limpiarFormulario()
    }

    // ---------- BUSCAR (ejercicio 1) ----------
    private fun buscar() {
        val id = txtId.text.toString().trim().toIntOrNull()
        if (id == null) {
            txtId.error = "Ingrese el código del producto"
            txtId.requestFocus()
            return
        }

        val c = managerProductos.searchProducto(id)
        if (c != null && c.moveToFirst()) {
            // columnas: 0=idproductos, 1=idcategoria, 2=descripcion, 3=precio, 4=cantidad
            txtIdDB.text = c.getInt(0).toString()
            val nombreCategoria = managerCategoria.searchNombre(c.getInt(1))
            val posicion = listaCategorias.indexOf(nombreCategoria)
            cmbCategorias.setSelection(if (posicion >= 0) posicion else 0)
            txtNombre.setText(c.getString(2))
            txtPrecio.setText(c.getString(3))
            txtCantidad.setText(c.getString(4))
            mostrar("Producto encontrado")
        } else {
            val codigo = txtId.text.toString()
            limpiarFormulario()
            txtId.setText(codigo)
            mostrar("No se encontraron registros")
        }
        c?.close()
    }

    // ---------- ACTUALIZAR (ejercicio 2) ----------
    private fun actualizar() {
        val id = txtIdDB.text.toString().trim().toIntOrNull()
        if (id == null) {
            mostrar("Primero busque un producto por su código")
            return
        }
        if (!validarProducto()) return

        if (!existeProducto(id)) {
            mostrar("No se ha encontrado el producto cargado")
            return
        }
        managerProductos.updateProducto(
            id,
            idCategoriaSeleccionada(),
            txtNombre.text.toString().trim(),
            txtPrecio.text.toString().trim().toDouble(),
            txtCantidad.text.toString().trim().toInt()
        )
        mostrar("Producto actualizado")
        limpiarFormulario()
    }

    // ---------- ELIMINAR (ejercicio 2) ----------
    private fun eliminar() {
        val id = txtIdDB.text.toString().trim().toIntOrNull()
        if (id == null) {
            mostrar("Primero busque un producto por su código")
            return
        }
        if (!existeProducto(id)) {
            mostrar("No se ha encontrado el producto cargado")
            return
        }
        AlertDialog.Builder(this)
            .setMessage("¿Desea eliminar el producto ${txtNombre.text}?")
            .setCancelable(false)
            .setPositiveButton("Sí") { _, _ ->
                managerProductos.deleteProducto(id)
                mostrar("Producto eliminado")
                limpiarFormulario()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // ---------- AUXILIARES ----------
    private fun validarProducto(): Boolean {
        var ok = true
        val precio = txtPrecio.text.toString().trim().toDoubleOrNull()
        val cantidad = txtCantidad.text.toString().trim().toIntOrNull()

        if (txtNombre.text.toString().trim().isEmpty()) {
            txtNombre.error = "Ingrese el nombre del producto"
            txtNombre.requestFocus()
            ok = false
        }
        if (precio == null || precio <= 0) {
            txtPrecio.error = "Ingrese un precio válido"
            txtPrecio.requestFocus()
            ok = false
        }
        if (cantidad == null || cantidad < 0) {
            txtCantidad.error = "Ingrese una cantidad válida"
            txtCantidad.requestFocus()
            ok = false
        }
        return ok
    }

    private fun existeProducto(id: Int): Boolean {
        val c = managerProductos.searchProducto(id)
        val existe = c != null && c.count > 0
        c?.close()
        return existe
    }

    private fun idCategoriaSeleccionada(): Int {
        val nombre = cmbCategorias.selectedItem.toString().trim()
        return managerCategoria.searchID(nombre) ?: 1
    }

    private fun limpiarFormulario() {
        txtId.setText("")
        txtIdDB.text = ""          // evita actualizar/eliminar un producto anterior
        txtNombre.setText("")
        txtPrecio.setText("")
        txtCantidad.setText("")
        cmbCategorias.setSelection(0)
    }

    private fun mostrar(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}