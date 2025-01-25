package pt.ipt.dam.pocketknowledge

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

        // Elemento Botao de Registo
        val registerButton: Button = findViewById(R.id.registerScreenButton)
        // Configurar o botao para abrir outra activity
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterScreenActivity::class.java)
            startActivity(intent)
        }

        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 colunas
        val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")
        val adapter = ItemAdapter(items, this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        // Ação quando um item é clicado
    }
}