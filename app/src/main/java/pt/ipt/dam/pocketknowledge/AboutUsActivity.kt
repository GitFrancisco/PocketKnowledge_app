package pt.ipt.dam.pocketknowledge

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_us)

        // Elemento Botao de Voltar
        val BackButton : Button = findViewById(R.id.BackButton)
        // Configurar o botao para fechar a activity
        BackButton.setOnClickListener {
            finish()
        }
    }
}