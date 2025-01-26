package pt.ipt.dam.pocketknowledge

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class all_flashcards_screenActivity : AppCompatActivity(), ItemAdapter.OnItemClickListener {

    private val items = listOf("Flashcard 1", "Flashcard 2", "Flashcard 3") // Lista de itens

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_flashcards_screen)

        // Inicializar o RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view) // Atualizado para "recycler_view"
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // Configurar o Adapter
        val adapter = ItemAdapter(items, this)
        recyclerView.adapter = adapter
    }

    // Implementação do onclick
    override fun onItemClick(position: Int) {
        // Mostra um Toast ao clicar no item
        Toast.makeText(this, "Item clicado: $position", Toast.LENGTH_SHORT).show()
    }
}
