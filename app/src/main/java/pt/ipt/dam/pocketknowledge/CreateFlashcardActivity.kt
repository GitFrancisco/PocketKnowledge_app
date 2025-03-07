package pt.ipt.dam.pocketknowledge

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.pocketknowledge.model.addFlashcard
import pt.ipt.dam.pocketknowledge.model.createFlashcardResponse
import pt.ipt.dam.pocketknowledge.model.userData
import pt.ipt.dam.pocketknowledge.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class CreateFlashcardActivity : AppCompatActivity() {
    private lateinit var questionInput: EditText
    private lateinit var answerInput: EditText
    private lateinit var createFlashcardButton: Button
    private var themeId by Delegates.notNull<Int>()
    private var userId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_flashcard)

        // Inicializar os elementos
        questionInput = findViewById(R.id.questionField)
        answerInput = findViewById(R.id.answerField)
        createFlashcardButton = findViewById(R.id.CreateFlashcardButton)
        createFlashcardButton.setOnClickListener(
            // Verificar se os campos estão preenchidos
            {
                val question = questionInput.text.toString()
                val answer = answerInput.text.toString()

                if (question.isNotEmpty() && answer.isNotEmpty()) {
                    createFlashcard(question, answer)
                } else {
                    Toast.makeText(this, getString(R.string.fillUpAllCamps), Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Obter o ID do Tema passado pela Intent
        themeId = intent.getIntExtra("THEME_ID", -1)

        fetchUserData()

    } // ON CREATE

    // Função para criar um flashcard
    private fun createFlashcard(question: String, answer: String) {
        // Recuperar o token guardado no SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        // Verifica se o token é nulo ou inválido
        if (token.isNullOrEmpty()) {
            Toast.makeText(applicationContext, getString(R.string.errorAuth), Toast.LENGTH_SHORT).show()
            return
        }

        // Criar uma instância do RetrofitInitializer e acessar o serviço da API
        val apiService = RetrofitInitializer().apiService()

        // Criar um objeto flashcard com os dados fornecidos
        val flashcard = addFlashcard(question, answer, userId)

        // Fazer uma chamada à API para criar um flashcard
        apiService.createFlashcard("Bearer $token", flashcard).enqueue(object : Callback<createFlashcardResponse> {
            override fun onResponse(call: Call<createFlashcardResponse>, response: Response<createFlashcardResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext,
                        getString(R.string.SuccFlashcard), Toast.LENGTH_SHORT).show()
                    // Obter o ID do flashcard criado
                    val flashcardId = response.body()?.id
                    if (flashcardId != null) {
                        // Mapear o flashcard para o tema
                        mapFlashcardToTheme(flashcardId)
                    }
                    // Redirecionar o utilizador para a MainFragmentActivity
                    val intent = Intent(applicationContext, MainFragmentActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.errorCreatFlashcards),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            // Exibe uma mensagem de erro ao utilizador
            override fun onFailure(call: Call<createFlashcardResponse>, t: Throwable) {
                Toast.makeText(applicationContext, R.string.serverConnectFail, Toast.LENGTH_SHORT).show()
            }
        })

    }

    // Função para buscar os dados do utilizador autenticado
    private fun fetchUserData() {
        // Recuperar o token guardado no SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

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
                        // Verificar se o utilizador é administrador
                        val role = userData.Role
                        if (role == "admin"){
                            userId = userData.id
                        } else {
                            Toast.makeText(applicationContext,
                                getString(R.string.adminCreateCards), Toast.LENGTH_SHORT).show()
                            // Redirecionar o utilizador para a activity de flashcards
                            val intent = Intent(applicationContext, all_flashcards_screenActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, R.string.userDataError, Toast.LENGTH_SHORT).show()
                }
            }

            // Exibe uma mensagem de erro ao utilizador
            override fun onFailure(call: Call<userData>, t: Throwable) {
                Toast.makeText(applicationContext, R.string.serverConnectFail, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mapFlashcardToTheme(flashcardId : Int) {
        // Recuperar o token guardado no SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        // Verifica se o token é nulo ou inválido
        if (token.isNullOrEmpty()) {
            Toast.makeText(applicationContext, R.string.errorAuth, Toast.LENGTH_SHORT).show()
            return
        }

        // Criar uma instância do RetrofitInitializer e acessar o serviço da API
        val apiService = RetrofitInitializer().apiService()

        // Criar um objeto mapFlashcardToTheme com os dados fornecidos
        val mapFlashcard =
            pt.ipt.dam.pocketknowledge.model.mapFlashcardToTheme(flashcardId, themeId)

        // Fazer uma chamada à API para mapear um flashcard para um tema
        apiService.mapFlashcardToTheme("Bearer $token", mapFlashcard).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }
            // Exibe uma mensagem de erro ao utilizador
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(applicationContext, R.string.serverConnectFail, Toast.LENGTH_SHORT).show()
            }
        })
    }
}