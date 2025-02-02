package pt.ipt.dam.pocketknowledge

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pt.ipt.dam.pocketknowledge.model.flashcards
import pt.ipt.dam.pocketknowledge.model.userData
import pt.ipt.dam.pocketknowledge.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class all_flashcards_screenActivity : AppCompatActivity(), ItemAdapter.OnItemClickListener {

    private var items = mutableListOf<flashcards>() // Lista de flashcards
    private lateinit var adapter: ItemAdapter // Adapter
    private lateinit var recyclerView: RecyclerView // RecyclerView
    private lateinit var createButton: Button // Botão de criar flashcard
    private lateinit var swipeRefresh: SwipeRefreshLayout // SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_flashcards_screen)

        // Inicializar o SwipeRefreshLayout
        swipeRefresh = findViewById(R.id.swipeRefresh)

        // Inicializar o RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // Configurar o Adapter
        adapter = ItemAdapter(items.map { it.question }, this)
        recyclerView.adapter = adapter

        // Obter o ID do Tema passado pela Intent
        val themeId = intent.getIntExtra("THEME_ID", -1)

        // Inicializar o botão de criar flashcard
        createButton = findViewById(R.id.CreateFlashcardButton)
        createButton.setOnClickListener {
            val intent = Intent(this, CreateFlashcardActivity::class.java)
            startActivity(intent)
            // Enviar o ID correto baseado na posição clicada
            intent.putExtra("THEME_ID", themeId)
            startActivity(intent) // Iniciar a nova Activity
        }

        // Buscar os flashcards da API
        fetchFlashcards(themeId)

        // Configura o refresh ao deslizar para baixo
        swipeRefresh.setOnRefreshListener {
            fetchFlashcards(themeId)// Atualiza os dados da API
            swipeRefresh.isRefreshing = false
        }

        // Buscar os dados do utilizador autenticado
        fetchUserData()
    }

    // Implementação do onclick
    override fun onItemClick(position: Int) {
        // Obter o flashcard selecionado
        val selectedFlashcard = items[position]
        // Implementação a abertura do flashcard
        val intent = Intent(this, inside_flashcardActivity::class.java)

        // Enviar o ID correto baseado na posição clicada
        intent.putExtra("FLASHCARD_ID", selectedFlashcard.id)
        startActivity(intent) // Iniciar a nova Activity
    }

    // Função para buscar os flashcards da API
    private fun fetchFlashcards(THEME_ID: Int) {
        // Recuperar o token guardado no SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        // Verifica se o token é nulo ou inválido
        if (token.isNullOrEmpty()) {
            Toast.makeText(applicationContext, getString(R.string.errorAuth), Toast.LENGTH_SHORT).show()
            return
        }
        // Fazer a chamada à API
        val call = RetrofitInitializer().apiService().getFlashcardsByTheme("Bearer $token",THEME_ID)

        call.enqueue(object : Callback<List<flashcards>> {
            // Callbacks
            override fun onResponse(call: Call<List<flashcards>>, response: Response<List<flashcards>>) {
                // Verifica se a resposta é bem sucedida
                if (response.isSuccessful) {
                    // Obter a lista de flashcards
                    val flashcards = response.body()
                    // Verifica se a lista não é nula
                    if (flashcards != null) {
                        // Atualiza a lista
                        items.clear()
                        items.addAll(flashcards)
                        adapter = ItemAdapter(items.map { it.question }, this@all_flashcards_screenActivity)
                        recyclerView.adapter = adapter // Atualizar o adapter com os novos dados
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(applicationContext,
                        getString(R.string.errorFlashcard), Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<flashcards>>, t: Throwable) {
                Toast.makeText(applicationContext,
                    getString(R.string.connectionError), Toast.LENGTH_SHORT).show()
            }
        })
    }

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
                        val role = userData.Role
                        if (role == "admin"){
                            // Se o utilizador for administrador, exibir o botão de criar flashcard
                            createButton.visibility = Button.VISIBLE
                        }
                    }
                } else {
                    Toast.makeText(applicationContext,
                        getString(R.string.userDataError), Toast.LENGTH_SHORT).show()
                }
            }

            // Exibe uma mensagem de erro ao utilizador
            override fun onFailure(call: Call<userData>, t: Throwable) {
                Toast.makeText(applicationContext,
                    getString(R.string.serverConnectFail), Toast.LENGTH_SHORT).show()
            }
        })
    }
}