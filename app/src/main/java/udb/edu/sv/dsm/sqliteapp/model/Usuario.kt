package udb.edu.sv.dsm.sqliteapp.model


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import udb.edu.sv.dsm.sqliteapp.db.HelperDB
import java.security.MessageDigest

class Usuario(context: Context?) {

    private val helper = HelperDB(context)
    private val db: SQLiteDatabase = helper.writableDatabase

    companion object {
        const val TABLE_NAME_USUARIO = "usuario"
        const val COL_ID = "idusuario"
        const val COL_NICK = "nick"
        const val COL_PASSWORD = "password"

        const val CREATE_TABLE_USUARIO = (
                "CREATE TABLE IF NOT EXISTS $TABLE_NAME_USUARIO("
                        + "$COL_ID integer primary key autoincrement,"
                        + "$COL_NICK varchar(50) NOT NULL UNIQUE,"
                        + "$COL_PASSWORD varchar(64) NOT NULL);"
                )
    }

    private fun hash(texto: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray())
            .joinToString("") { "%02x".format(it) }

    fun existeNick(nick: String): Boolean {
        val c = db.query(
            TABLE_NAME_USUARIO, arrayOf(COL_ID),
            "$COL_NICK=?", arrayOf(nick), null, null, null
        )
        val existe = c.count > 0
        c.close()
        return existe
    }

    // true si se registró, false si el nick ya existe o falló el insert
    fun registrar(nick: String, password: String): Boolean {
        if (existeNick(nick)) return false
        val valores = ContentValues().apply {
            put(COL_NICK, nick)
            put(COL_PASSWORD, hash(password))
        }
        return db.insert(TABLE_NAME_USUARIO, null, valores) != -1L
    }

    fun validarLogin(nick: String, password: String): Boolean {
        val c = db.query(
            TABLE_NAME_USUARIO, arrayOf(COL_ID),
            "$COL_NICK=? AND $COL_PASSWORD=?", arrayOf(nick, hash(password)),
            null, null, null
        )
        val ok = c.count > 0
        c.close()
        return ok
    }
}