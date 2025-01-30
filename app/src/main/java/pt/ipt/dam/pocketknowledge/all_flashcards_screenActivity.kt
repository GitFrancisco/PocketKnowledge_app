package pt.ipt.dam.pocketknowledge

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam.pocketknowledge.model.flashcards
import pt.ipt.dam.pocketknowledge.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class all_flashcards_screenActivity : AppCompatActivity(), ItemAdapter.OnItemClickListener {

    private var items = mutableListOf<String>() // Lista de flashcards
    private lateinit var adapter: ItemAdapter // Adapter
    private lateinit var recyclerView: RecyclerView // RecyclerView

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

        // Buscar os flashcards da API
        fetchFlashcards()
    }

    // Implementação do onclick
    override fun onItemClick(position: Int) {
        Toast.makeText(this, "Item clicado: ${items[position]}", Toast.LENGTH_SHORT).show()
        // Implementação a abertura do flashcard (+1 no ID para corresponder com a base de dados)


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
                        adapter.notifyDataSetChanged() // Notifica a atualização dos dados
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
}