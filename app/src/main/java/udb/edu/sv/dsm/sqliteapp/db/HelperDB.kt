package udb.edu.sv.dsm.sqliteapp.db


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import udb.edu.sv.dsm.sqliteapp.model.Categoria
import udb.edu.sv.dsm.sqliteapp.model.Productos
import udb.edu.sv.dsm.sqliteapp.model.Usuario

class HelperDB(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "tienda.sqlite"
        private const val DB_VERSION = 2          // antes era 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Categoria.CREATE_TABLE_CATEGORIA)
        db.execSQL(Productos.CREATE_TABLE_PRODUCTOS)
        db.execSQL(Usuario.CREATE_TABLE_USUARIO)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(Usuario.CREATE_TABLE_USUARIO)
        }
    }
}
