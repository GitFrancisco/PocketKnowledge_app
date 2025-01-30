package pt.ipt.dam.pocketknowledge

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
            val intent = Intent(this, RegisterScreenActivity::class.java)
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
                        // Exibe um Toast de boas-vindas ao utilizador
                        welcomeUser()
                        // Redireciona o utilizador para a activity de flashcards
                        val intent = Intent(applicationContext, all_flashcards_screenActivity::class.java)
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

    // Exibe uma mensagem de de boas-vindas ao utilizador
    private fun welcomeUser() {
        // Recuperar o token salvo no SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token != null) {
            // Criar uma instância do RetrofitInitializer e acessar o serviço da API
            val apiService = RetrofitInitializer().apiService()

            // Fazer uma chamada à API para obter os dados do utilizador autenticado
            apiService.getUserData("Bearer $token").enqueue(object : Callback<userData> {
                override fun onResponse(call: Call<userData>, response: Response<userData>) {
                    if (response.isSuccessful) {
                        // Obter os dados do utilizador
                        val userData = response.body()
                        // Verificar se os dados do utilizador não são nulos
                        if (userData != null) {
                            // Exibir o nome do usuário no Toast
                            Toast.makeText(applicationContext, "Bem-vindo, ${userData.username}!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Erro ao buscar dados do utilizador.", Toast.LENGTH_SHORT).show()
                    }
                }

                // Exibe uma mensagem de erro ao utilizador
                override fun onFailure(call: Call<userData>, t: Throwable) {
                    Toast.makeText(applicationContext, "Erro ao conectar ao servidor.", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(applicationContext, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show()
        }
    }
}

