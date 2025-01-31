import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam.pocketknowledge.ItemAdapter
import pt.ipt.dam.pocketknowledge.R
import pt.ipt.dam.pocketknowledge.inside_flashcardActivity
import pt.ipt.dam.pocketknowledge.model.flashcards
import pt.ipt.dam.pocketknowledge.model.themes
import pt.ipt.dam.pocketknowledge.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CategoriesFragment : Fragment(R.layout.categories_screen), ItemAdapter.OnItemClickListener{

    private var items = mutableListOf<themes>() // Lista de flashcards
    private lateinit var adapter: ItemAdapter // Adapter
    private lateinit var recyclerView: RecyclerView // RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar o RecyclerView
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        // Configurar o Adapter
        adapter = ItemAdapter(items.map { it.theme }, this)
        recyclerView.adapter = adapter

        // Buscar os temas da API e atualizar a lista
        fetchThemes()
    }

    // Implementação do onclick
    override fun onItemClick(position: Int) {
        // Obter o tema selecionado
        val selectedTheme = items[position]
        // Implementação a abertura do tema
        Toast.makeText(requireContext(), "Tema selecionado: ${selectedTheme.theme}", Toast.LENGTH_SHORT).show()
    }

    // Buscar os temas da API
    // Função para buscar os flashcards da API
    private fun fetchThemes() {
        // Fazer a chamada à API
        val call = RetrofitInitializer().apiService().getThemes()

        call.enqueue(object : Callback<List<themes>> {
            // Callbacks
            override fun onResponse(call: Call<List<themes>>, response: Response<List<themes>>) {
                // Verifica se a resposta é bem sucedida
                if (response.isSuccessful) {
                    // Obter a lista de temas
                    val themes = response.body()
                    // Verifica se a lista não é nula
                    if (themes != null) {
                        // Atualiza a lista
                        items.clear()
                        items.addAll(themes)
                        adapter = ItemAdapter(items.map { it.theme }, this@CategoriesFragment)
                        recyclerView.adapter = adapter // Atualizar o adapter com os novos dados
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro ao carregar temas...", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<themes>>, t: Throwable) {
                Toast.makeText(requireContext(), "Falha na conexão...", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
