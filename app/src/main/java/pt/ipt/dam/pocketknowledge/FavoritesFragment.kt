import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.ipt.dam.pocketknowledge.ItemAdapter
import pt.ipt.dam.pocketknowledge.R
import pt.ipt.dam.pocketknowledge.all_flashcards_screenActivity
import pt.ipt.dam.pocketknowledge.inside_flashcardActivity
import pt.ipt.dam.pocketknowledge.model.flashcards
import pt.ipt.dam.pocketknowledge.model.themes
import pt.ipt.dam.pocketknowledge.retrofit.RetrofitInitializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesFragment : Fragment(R.layout.favorite_flashcards_screen), ItemAdapter.OnItemClickListener {

    private var items = mutableListOf<flashcards>() // Lista de flashcards
    private lateinit var adapter: ItemAdapter // Adapter
    private lateinit var recyclerView: RecyclerView // RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Inicializar o RecyclerView
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        // Configurar o Adapter
        adapter = ItemAdapter(items.map { it.question }, this)
        recyclerView.adapter = adapter

        // Buscar os temas da API e atualizar a lista
        fetchFavorites()
    }

    // Implementação do onclickx
    override fun onItemClick(position: Int) {
        // Obter o tema selecionado
        val selectedFlashcard = items[position]
        // Implementação a abertura da lista de flashcards baseados nos favoritos
        val intent = Intent(requireContext(), inside_flashcardActivity::class.java)
        // Enviar o ID correto baseado na posição clicada
        intent.putExtra("FLASHCARD_ID", selectedFlashcard.id)
        startActivity(intent) // Iniciar a nova Activity
    }

    // Função para buscar os flashcards favoritos da API
    private fun fetchFavorites() {
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token.isNullOrEmpty()) {
            return
        }

        // Fazer a chamada à API
        val call = RetrofitInitializer().apiService().getFavoriteFlashcards("Bearer $token")

        call.enqueue(object : Callback<List<flashcards>> {
            override fun onResponse(call: Call<List<flashcards>>, response: Response<List<flashcards>>) {
                if (response.isSuccessful) {
                    // Obter a lista de flashcards favoritos
                    val flashcards = response.body()

                    if (flashcards != null) {
                        // Atualiza a lista
                        items.clear()
                        items.addAll(flashcards)
                        adapter = ItemAdapter(items.map { it.question }, this@FavoritesFragment)
                        recyclerView.adapter = adapter // Atualizar o adapter com os novos dados
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro ao carregar flashcards favoritos...", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<flashcards>>, t: Throwable) {
                Toast.makeText(requireContext(), "Falha na conexão...", Toast.LENGTH_SHORT).show()
            }
        })
    }



}
