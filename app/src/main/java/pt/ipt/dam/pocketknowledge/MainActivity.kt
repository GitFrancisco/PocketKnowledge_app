package pt.ipt.dam.pocketknowledge

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pt.ipt.dam.pocketknowledge.model.authResponse
import pt.ipt.dam.pocketknowledge.model.userData
import pt.ipt.dam.pocketknowledge.model.userLogin
import pt.ipt.dam.pocketknowledge.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Verifica se já existe um token de autenticação em SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)
        if (token != null) {
            // Verifica se o token ainda é válido
            verifyTokenAndRedirect(token)
        }


        // Elemento input email
        val emailInput: EditText = findViewById(R.id.emailField)
        // Elemento input password
        val passwordInput: EditText = findViewById(R.id.passwordField)

        // Elemento Botao de Login
        val loginButton: Button = findViewById(R.id.loginButton)
        // Configurar o botao para abrir outra activity
        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Preencha todos os campos...", Toast.LENGTH_SHORT).show()
            }
        }


        // Elemento Botao de Registo
        val registerButton: Button = findViewById(R.id.registerScreenButton)
        // Configurar o botao para abrir outra activity
        registerButton.setOnClickListener {
            val intent = Intent(this, all_flashcards_screenActivity::class.java)
            startActivity(intent)
        }

        // Elemento botao "about us"
        val aboutUsButton: ImageButton = findViewById(R.id.aboutUsButton)
        // Configurar o botao para abrir outra activity
        aboutUsButton.setOnClickListener {
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
        }

    } // ON CREATE

    // Função para autenticar o utilizador
    private fun loginUser(email: String, password: String) {
        val userLogin = userLogin(email, password)

        // Cria uma instância do RetrofitInitializer e acessar o serviço da API
        val apiService = RetrofitInitializer().apiService()

        // Fazer uma chamada à API para autenticar o utilizador
        apiService.login(userLogin).enqueue(object : Callback<authResponse> {
            override fun onResponse(call: Call<authResponse>, response: Response<authResponse>) {
                if (response.isSuccessful) {
                    // Obter a resposta da API
                    val authResponse = response.body()
                    if (authResponse != null) {
                        // Guarda a JWT Token no SharedPreferences
                        saveToken(authResponse.token)
                        // Redireciona o utilizador para a activity de flashcards
                        val intent = Intent(applicationContext, MainFragmentActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    // Exibe uma mensagem de erro ao utilizador
                    Toast.makeText(applicationContext, "Falha no login...", Toast.LENGTH_SHORT).show()
                }
            }

            // Exibe uma mensagem de erro ao utilizador
            override fun onFailure(call: Call<authResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro ao autenticar...", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Guarda a JWT Token no SharedPreferences
    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.apply()
    }

    // Função para verificar se o token ainda é válido e redirecionar o utilizador
    private fun verifyTokenAndRedirect(token: String) {
        val apiService = RetrofitInitializer().apiService()

        // Fazer uma chamada assíncrona para verificar se o token é válido
        apiService.getUserData("Bearer $token").enqueue(object : Callback<userData> {
            override fun onResponse(call: Call<userData>, response: Response<userData>) {
                if (response.isSuccessful && response.body() != null) {
                    // Token válido, redirecionar o utilizador
                    val intent = Intent(applicationContext, MainFragmentActivity::class.java)
                    startActivity(intent)
                    finish() // Finaliza a MainActivity para evitar que o utilizador volte ao login
                } else {
                    Toast.makeText(applicationContext, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<userData>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro ao verificar sessão. Faça login novamente.", Toast.LENGTH_SHORT).show()
            }
        })
    }

}

