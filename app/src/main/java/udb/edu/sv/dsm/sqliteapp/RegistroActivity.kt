package udb.edu.sv.dsm.sqliteapp


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import udb.edu.sv.dsm.sqliteapp.model.Usuario

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val txtNick = findViewById<EditText>(R.id.txtNickReg)
        val txtPassword = findViewById<EditText>(R.id.txtPasswordReg)
        val txtConfirm = findViewById<EditText>(R.id.txtPasswordConfirm)
        val btnCrear = findViewById<Button>(R.id.btnCrearCuenta)
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        val usuario = Usuario(this)

        btnCrear.setOnClickListener {
            val nick = txtNick.text.toString().trim()
            val password = txtPassword.text.toString()
            val confirmar = txtConfirm.text.toString()

            if (nick.length < 3) {
                txtNick.error = "El nick debe tener al menos 3 caracteres"
                return@setOnClickListener
            }
            if (password.length < 4) {
                txtPassword.error = "La contraseña debe tener al menos 4 caracteres"
                return@setOnClickListener
            }
            if (password != confirmar) {
                txtConfirm.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }

            if (usuario.registrar(nick, password)) {
                Toast.makeText(this, "Cuenta creada, inicie sesión", Toast.LENGTH_LONG).show()
                finish()   // vuelve al login
            } else {
                txtNick.error = "Ese nick ya está registrado"
            }
        }

        btnVolver.setOnClickListener { finish() }
    }
}