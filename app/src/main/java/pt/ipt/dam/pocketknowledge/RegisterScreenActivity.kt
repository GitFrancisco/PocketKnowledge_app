package pt.ipt.dam.pocketknowledge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.pocketknowledge.model.userRegister
import pt.ipt.dam.pocketknowledge.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_screen)

        // Elemento name field
        val username: EditText = findViewById(R.id.UserField_Reg)

        // Elemento email field
        val email: EditText = findViewById(R.id.emailField_Reg)

        // Elemento password field
        val password: EditText = findViewById(R.id.passwordField_Reg)

        // Elemento confirm password field
        val passwordConfirmation: EditText = findViewById(R.id.passwordConfField_Reg)

        // Elemento Botao de voltar ao login
        val backToLoginScreenButton: Button = findViewById(R.id.backtologinButton)
        // Configurar o botao para abrir outra activity
        backToLoginScreenButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Elemento Botao de registar
        val registerButton: Button = findViewById(R.id.registerButton)
        registerButton.setOnClickListener {
            // Registar o utilizador
            registerUser(username.text.toString(), email.text.toString(), password.text.toString(), passwordConfirmation.text.toString())
        }
    }

    // Registar um novo utilizador
    private fun registerUser(username: String, email: String, password: String, passwordConfirmation: String) {
        if (password != passwordConfirmation) {
            Toast.makeText(this, "As passwords não coincidem...", Toast.LENGTH_SHORT).show()
            return
        }

        // Criar um objeto userRegister
        val userRegister = userRegister(username, password, email)

        // Inicializar o serviço da API
        val apiService = RetrofitInitializer().apiService()

        // Fazer o pedido de registo
        apiService.register(userRegister).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Verificar se o pedido foi bem sucedido
                if (response.isSuccessful) {
                    // Mostrar mensagem de sucesso
                    Toast.makeText(applicationContext, "Utilizador registado com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(applicationContext, "Erro ao registar utilizador: $errorBody", Toast.LENGTH_LONG).show()
                }
            }
            // Caso ocorra um erro
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

}