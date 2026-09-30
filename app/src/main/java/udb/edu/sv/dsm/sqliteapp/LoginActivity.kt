package udb.edu.sv.dsm.sqliteapp


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import udb.edu.sv.dsm.sqliteapp.model.Usuario

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val txtNick = findViewById<EditText>(R.id.txtNick)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnIrRegistro = findViewById<Button>(R.id.btnIrRegistro)
        val usuario = Usuario(this)

        btnIngresar.setOnClickListener {
            val nick = txtNick.text.toString().trim()
            val password = txtPassword.text.toString()

            if (nick.isEmpty()) {
                txtNick.error = "Ingrese su nick"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                txtPassword.error = "Ingrese su contraseña"
                return@setOnClickListener
            }

            if (usuario.validarLogin(nick, password)) {
                Toast.makeText(this, "Bienvenido, $nick", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()   // para que "atrás" no regrese al login
            } else {
                Toast.makeText(this, "Nick o contraseña incorrectos", Toast.LENGTH_LONG).show()
            }
        }

        btnIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }
}