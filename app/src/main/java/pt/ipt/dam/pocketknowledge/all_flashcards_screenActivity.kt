package pt.ipt.dam.pocketknowledge

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam.pocketknowledge.model.flashcards
import pt.ipt.dam.pocketknowledge.model.userData
import pt.ipt.dam.pocketknowledge.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class all_flashcards_screenActivity : AppCompatActivity(), ItemAdapter.OnItemClickListener {

    private var items = mutableListOf<String>() // Lista de flashcards
    private lateinit var adapter: ItemAdapter // Adapter
    private lateinit var recyclerView: RecyclerView // RecyclerView
    private lateinit var createButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_flashcards_screen)

        // Inicializar o RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // Configurar o Adapter
        adapter = ItemAdapter(items, this)
        recyclerView.adapter = adapter

        // Inicializar o botão de criar flashcard
        createButton = findViewById(R.id.CreateFlashcardButton)
        createButton.setOnClickListener {
            val intent = Intent(this, CreateFlashcardActivity::class.java)
            startActivity(intent)
        }

        // Buscar os flashcards da API
        fetchFlashcards()
        // Buscar os dados do utilizador autenticado
        fetchUserData()
    }

    // Implementação do onclick
    override fun onItemClick(position: Int) {
        //Toast.makeText(this, "Item clicado: ${items[position]}", Toast.LENGTH_SHORT).show()
        // Implementação a abertura do flashcard (+1 no ID para corresponder com a base de dados)
        val intent = Intent(this, inside_flashcardActivity::class.java)

        // Enviar o ID correto baseado na posição clicada
        intent.putExtra("FLASHCARD_ID", position + 1)
        startActivity(intent) // Iniciar a nova Activity

    }

    // Função para buscar os flashcards da API
    private fun fetchFlashcards() {
        // Fazer a chamada à API
        val call = RetrofitInitializer().apiService().getFlashcards()

        call.enqueue(object : Callback<List<flashcards>> {
            // Callbacks
            override fun onResponse(call: Call<List<flashcards>>, response: Response<List<flashcards>>) {
                // Verifica se a resposta é bem sucedida
                if (response.isSuccessful) {
                    // Obter a lista de flashcards
                    val flashcards = response.body()
                    // Verifica se a lista não é nula
                    if (flashcards != null) {
                        // Atualiza a lista e notifica o adapter
                        items.clear()
                        items.addAll(flashcards.map { it.question })
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(applicationContext, "Erro ao carregar flashcards...", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<flashcards>>, t: Throwable) {
                Toast.makeText(applicationContext, "Falha na conexão...", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(applicationContext, "Erro ao buscar dados do utilizador.", Toast.LENGTH_SHORT).show()
                }
            }

            // Exibe uma mensagem de erro ao utilizador
            override fun onFailure(call: Call<userData>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro ao conectar ao servidor. Reinicie a aplicação.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}